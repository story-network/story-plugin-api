package com.storycraft.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.map.render.OffsetArea;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.map.CraftMapCanvas;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCursor;

import net.minecraft.server.v1_14_R1.MapIcon;
import net.minecraft.server.v1_14_R1.PacketPlayOutMap;

public class CustomMapManager extends MiniPlugin implements Listener {

    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService renderPool;

    static {
        renderPool = Executors.newFixedThreadPool(NUM_CORES * 2);
    }

    protected static ExecutorService getRenderpool() {
        return renderPool;
    }

    private Map<Integer, CustomMapData> idMap;
    private Map<Integer, CustomMapTracker> trackerMap;

    public CustomMapManager() {
        idMap = new ConcurrentHashMap<>();
        trackerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        
    }
    
    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public boolean containsId(int id) {
        return idMap.containsKey(id);
    }

    public World getDefaultWorld() {
        return getPlugin().getDefaultWorld();
    }

    public int getLatestMapId() {
        return ((CraftWorld) getDefaultWorld()).getHandle().getWorldMapCount();
    }

    public boolean addCustomMap(int id, CustomMapData data) {
        if (containsId(id))
            return false;

        CustomMapTracker tracker = new CustomMapTracker(id, this::onTrackerAdded, this::onTrackerRemoved);

        idMap.put(id, data);
        trackerMap.put(id, tracker);

        return true;
    }

    public CustomMapData getCustomMap(int id) {
        if (!containsId(id))
            return null;

        return idMap.get(id);
    }

    public void removeCustomMap(int id) {
        idMap.remove(id);
        trackerMap.remove(id);
    }

    protected CompletableFuture<Void> updateInternal(CustomMapData data) {
        return CompletableFuture.runAsync(() -> {
            renderProcessTask(data, data.getRenderer().getDirtyArea());
        }, getRenderpool());
    }

    protected void renderProcessTask(CustomMapData data, Collection<OffsetArea> dirtyArea) {
        for (OffsetArea area : dirtyArea) {
            data.renderToBuffer(area);
        }
    }

    protected void sendEntireMapPacket(Player p, int id, CustomMapData data) {
        Collection<MapIcon> iconCollection = getIconCollection(data);

        PacketPlayOutMap mapPacket = new PacketPlayOutMap(id, data.getScale().getByteSize(), data.getShouldTrack(), data.isLocked(), iconCollection
                , data.getBuffer(), 0, 0, 128, 128);

        ConnectionUtil.sendPacket(p, mapPacket);
    }

    protected void sendDirtyMapPacket(Player p, int id, CustomMapData data) {
        Collection<MapIcon> iconCollection = getIconCollection(data);

        for (OffsetArea area : data.getRenderer().getDirtyArea()){

            PacketPlayOutMap mapPacket = new PacketPlayOutMap(id, data.getScale().getByteSize(), data.getShouldTrack(), data.isLocked(), iconCollection
                , data.getBuffer(), area.getY(), area.getX(), area.getSizeX(), area.getSizeY());

            ConnectionUtil.sendPacket(p, mapPacket);
        }
    }

    protected Collection<MapIcon> getIconCollection(CustomMapData data) {
        int iconSize = data.getCursorCollection().size();
        Collection<MapIcon> iconCollection = new ArrayList<>(iconSize);

        for (int i = 0; i < iconSize; i++) {
            MapCursor cursor = data.getCursorCollection().getCursor(i);
        }

        return iconCollection;
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {
        Collection<Player> playerList = (Collection<Player>) getPlugin().getServer().getOnlinePlayers();
        
        for (int id : idMap.keySet()) {
            CustomMapData data = idMap.get(id);
            CustomMapTracker tracker = trackerMap.get(id);

            tracker.update(playerList);

            if (data.getRenderer().needRender()) {
                updateInternal(data).thenRun(() -> {
                    for (Player p : tracker.getPlayerList()) {
                        sendDirtyMapPacket(p, id, data);
                    }
    
                    data.getRenderer().clearDirtyArea();
                });
            }
        }
    }

    protected Void onTrackerAdded(CustomMapTracker tracker, Player p) {
        sendEntireMapPacket(p, tracker.getMapId(), getCustomMap(tracker.getMapId()));

        return null;
    }

    protected Void onTrackerRemoved(CustomMapTracker tracker, Player p) {

        return null;
    }

}