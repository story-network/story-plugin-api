package com.storycraft.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.map.render.OffsetArea;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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

    private WrappedField<byte[], PacketPlayOutMap> dataField;

    public CustomMapManager() {
        idMap = new ConcurrentHashMap<>();
        dataField = Reflect.getField(PacketPlayOutMap.class, "j");
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

        idMap.put(id, data);

        return true;
    }

    public CustomMapData getCustomMap(int id) {
        if (!containsId(id))
            return null;

        return idMap.get(id);
    }

    public void removeCustomMap(int id) {
        idMap.remove(id);
    }

    public void update(int id) {

    }

    public void update(Player p, int id) {
        if (!containsId(id))
            return;

        CustomMapData data = getCustomMap(id);

        getRenderpool().execute(() -> {
            renderProcessTask(p, id, data, data.getRenderer().getDirtyArea());
        });

    }

    protected void renderProcessTask(Player p, int id, CustomMapData data, Collection<OffsetArea> areaList) {
        if (data.getRenderer() == null)
            return;

        for (OffsetArea area : areaList) {

            byte[] renderData = data.getRenderer().render(area);

            List<MapIcon> iconCollection = new ArrayList<>();

            PacketPlayOutMap mapPacket = new PacketPlayOutMap(id, data.getScale().getByteSize(), data.getShouldTrack(), data.isLocked(), iconCollection
                , new byte[(area.getY() + area.getSizeY()) * (area.getX() + area.getSizeY())], area.getY(), area.getX(), area.getSizeX(), area.getSizeY());

            dataField.set(mapPacket, renderData);

            ConnectionUtil.sendPacket(p, mapPacket);
        }

        
    }

}