package com.storycraft.server.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.storycraft.MainMiniPlugin;
import com.storycraft.MainPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.server.map.render.IMapRenderer;
import com.storycraft.server.map.render.OffsetArea;
import com.storycraft.util.ConnectionUtil;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.minecraft.server.v1_14_R1.MapIcon;

public class CustomMapManager extends MainMiniPlugin implements Listener {

    private Map<Integer, ManagedCustomMap> idMap;

    public CustomMapManager() {
        idMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onLoad(MainPlugin plugin) {

    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean restart) {
        for (int id : idMap.keySet()) {
            removeCustomMap(id);
        }
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

        MapView bukkitView = getPlugin().getServer().getMap(id);

        while (getLatestMapId() < id) {
            bukkitView = getPlugin().getServer().createMap(getDefaultWorld());
        }

        ManagedCustomMap managed = new ManagedCustomMap(data, bukkitView, new BukkitMapRenderer(data), new ArrayList<>(bukkitView.getRenderers()));

        idMap.put(id, managed);

        if (!bukkitView.getRenderers().isEmpty()) {
            for (MapRenderer renderer : managed.getDefaultRendererList()) {
                bukkitView.removeRenderer(renderer);
            }
        }

        bukkitView.addRenderer(managed.getBukkitRenderer());

        return true;
    }

    public CustomMapData getCustomMap(int id) {
        if (!containsId(id))
            return null;

        return idMap.get(id).getMapData();
    }

    public boolean removeCustomMap(int id) {
        if (!containsId(id))
            return false;

        ManagedCustomMap managed = idMap.remove(id);

        managed.getBukkitView().removeRenderer(managed.getBukkitRenderer());

        for (MapRenderer renderer : managed.getDefaultRendererList()) {
            managed.getBukkitView().addRenderer(renderer);
        }

        return true;
    }

    protected Collection<MapIcon> getIconCollection(CustomMapData data) {
        int iconSize = data.getCursorCollection().size();
        Collection<MapIcon> iconCollection = new ArrayList<>(iconSize);

        for (int i = 0; i < iconSize; i++) {
            MapCursor cursor = data.getCursorCollection().getCursor(i);
        }

        return iconCollection;
    }

    protected class ManagedCustomMap {

        private BukkitMapRenderer bukkitRenderer;
        private MapView bukkitView;
        private CustomMapData mapData;
        private List<MapRenderer> defaultRendererList;
        
        public ManagedCustomMap(CustomMapData mapData, MapView bukkitView, BukkitMapRenderer bukkitRenderer, List<MapRenderer> defaultRendererList) {
            this.mapData = mapData;
            this.bukkitView = bukkitView;
            this.bukkitRenderer = bukkitRenderer;
            this.defaultRendererList = defaultRendererList;
        }

        public BukkitMapRenderer getBukkitRenderer() {
            return bukkitRenderer;
        }

        public MapView getBukkitView() {
            return bukkitView;
        }

        public CustomMapData getMapData() {
            return mapData;
        }

        public List<MapRenderer> getDefaultRendererList() {
            return defaultRendererList;
        }
    }

    public class BukkitMapRenderer extends MapRenderer {

        private CustomMapData mapData;

        public BukkitMapRenderer(CustomMapData mapData) {
            this.mapData = mapData;
        }

        public CustomMapData getMapData() {
            return mapData;
        }

        @Override
        public void render(MapView view, MapCanvas canvas, Player player) {
            IMapRenderer renderer = this.mapData.getRenderer();
            
            if (renderer.needRender()) {
                for (OffsetArea area : renderer.getDirtyArea()) {
                    byte[] data = renderer.render(area);

                    int x = 0, y = 0;
                    for (int offsetX = 0; offsetX < area.getSizeX(); offsetX++) {
                        for (int offsetY = 0; offsetY < area.getSizeY(); offsetY++) {
                            x = area.getX() + offsetX;
                            y = area.getY() + offsetY;

                            canvas.setPixel(x, y, data[offsetY * area.getSizeY() + offsetX]);
                        }
                    }
                }
            }
        }

    }

}