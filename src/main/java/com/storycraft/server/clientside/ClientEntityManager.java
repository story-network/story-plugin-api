package com.storycraft.server.clientside;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.event.client.AsyncPlayerLoadChunkEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientEntityManager extends ServerExtension implements Listener {

    private Map<String, List<Entity>> globalEntityMap;
    private Map<UUID, PlayerEntityTracker> playerTrackerMap;

    public ClientEntityManager(){
        this.globalEntityMap = new ConcurrentHashMap<>();
        this.playerTrackerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onDisable(boolean reload){
        for (String name : globalEntityMap.keySet()){
            org.bukkit.World w = getPlugin().getServer().getWorld(name);

            if (w == null) {
                continue;
            }

            for (Entity e : globalEntityMap.get(w.getName())){
                if (e != null)
                    sendDestroyPacket(e);
            }
        }

        globalEntityMap.clear();
    }

    @Override
    public void onEnable(){
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            playerTrackerMap.put(p.getUniqueId(), new PlayerEntityTracker(p));
        }
        
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void addClientEntity(Entity e){
        if (containsGlobal(e))
            return;

        if (!hasWorld(e.getWorld().getWorld())) {
            globalEntityMap.put(e.getWorld().getWorld().getName(), new ArrayList<>());
        }

        globalEntityMap.get(e.getWorld().getWorld().getName()).add(e);
        sendSpawnPacket(e);
        sendUpdatePacket(e);
    }

    public void removeClientEntity(Entity e){
        if (!containsGlobal(e))
            return;

        globalEntityMap.get(e.getWorld().getWorld().getName()).remove(e);
        sendDestroyPacket(e);
    }

    public void addClientEntity(Entity e, Player... pList) {
        for (Player p : pList)
            getPlayerTracker(p).addClientEntity(e);
    }

    public void removeClientEntity(Entity e, Player... pList) {
        for (Player p : pList)
            getPlayerTracker(p).removeClientEntity(e);
    }

    public boolean containsGlobal(Entity e){
        return hasWorld(e.getWorld().getWorld()) && globalEntityMap.get(e.getWorld().getWorld().getName()).contains(e);
    }

    protected boolean hasWorld(World w){
        return globalEntityMap.containsKey(w.getName());
    }

    public PlayerEntityTracker getPlayerTracker(Player p) {
        return playerTrackerMap.get(p.getUniqueId());
    }

    protected List<Entity> getGlobalWorldList(World w) {
        List<Entity> list;

        if (!globalEntityMap.containsKey(w.getName())) {
            globalEntityMap.put(w.getName(), list = new ArrayList<>());
        }
        else {
            list = globalEntityMap.get(w.getName());
        }

        return list;
    }

    public void update(Entity e){
        sendUpdatePacket(e);
    }

    protected void sendSpawnPacket(Player p, Entity e){
        ConnectionUtil.sendPacket(p, PacketUtil.getEntitySpawnPacket(e));
    }

    protected void sendSpawnPacket(Entity e){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, PacketUtil.getEntitySpawnPacket(e));
    }

    protected void sendUpdatePacket(Entity e){
        sendUpdatePacket(e, false);
    }

    protected void sendUpdatePacket(Entity e, boolean all){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, PacketUtil.getEntityMetadataPacket(e, all));
    }

    protected void sendUpdatePacket(Player p, Entity e){
        sendUpdatePacket(p, e, false);
    }

    protected void sendUpdatePacket(Player p, Entity e, boolean all){
        ConnectionUtil.sendPacket(p, PacketUtil.getEntityMetadataPacket(e, all));
    }

    protected void sendDestroyPacket(Entity e){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, PacketUtil.getEntityDestroyPacket(e));
    }

    protected void sendDestroyPacket(Player p, Entity e){
        ConnectionUtil.sendPacket(p, PacketUtil.getEntityDestroyPacket(e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerTrackerMap.remove(e.getPlayer().getUniqueId());
        playerTrackerMap.put(e.getPlayer().getUniqueId(), new PlayerEntityTracker(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerTrackerMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChunkLoad(AsyncPlayerLoadChunkEvent e) {
        if (!e.isFullChunk() || e.isCancelled() || e.getPlayer() == null)
            return;

        getPlayerTracker(e.getPlayer()).onPlayerChunkLoad(e.getChunkX(), e.getChunkZ());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent e) {
        if (e.getPlayer() == null)
            return;

        getPlayerTracker(e.getPlayer()).onPlayerRespawn(e.getRespawnedLocation());
    }

    public class PlayerEntityTracker {

        private Map<String, List<Entity>> entityMap;

        private Player player;

        protected PlayerEntityTracker(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        public World getPlayerWorld() {
            return player.getWorld();
        }

        public boolean containsPlayer(Entity e){
            return hasWorld(e.getWorld().getWorld()) && entityMap.get(e.getWorld().getWorld().getName()).contains(e);
        }
    
        protected boolean hasWorld(World w){
            return entityMap.containsKey(w.getName());
        }

        public void addClientEntity(Entity e){
            if (containsPlayer(e))
                return;
    
            if (!hasWorld(e.getWorld().getWorld())) {
                entityMap.put(e.getWorld().getWorld().getName(), new ArrayList<>());
            }
    
            entityMap.get(e.getWorld().getWorld().getName()).add(e);

            sendSpawnPacket(getPlayer(), e);
            sendUpdatePacket(getPlayer(), e);
        }
    
        public void removeClientEntity(Entity e){
            if (!containsPlayer(e))
                return;
    
            entityMap.get(e.getWorld().getWorld().getName()).remove(e);
            sendDestroyPacket(getPlayer(), e);
        }

        protected List<Entity> getWorldList(World w) {
            List<Entity> list;
    
            if (!entityMap.containsKey(w.getName())) {
                entityMap.put(w.getName(), list = new ArrayList<>());
            }
            else {
                list = entityMap.get(w.getName());
            }
    
            return list;
        }

        protected void onPlayerChunkLoad(int chunkX, int chunkZ) {
            List<Entity> list = new ArrayList<>(getGlobalWorldList(getPlayerWorld()));
            //list.removeAll(Collections.singleton(null));

            list.addAll(getGlobalWorldList(getPlayerWorld()));
    
            for (Entity entity : list) {
                if (entity == null) {
                    continue;
                }
    
                if (chunkX == ((int) Math.floor(entity.locX) >> 4)
                        && chunkZ == (int) Math.floor(entity.locZ) >> 4) {
                    sendDestroyPacket(getPlayer(), entity);
                    sendSpawnPacket(getPlayer(), entity);
                    sendUpdatePacket(getPlayer(), entity, true);
                }
            }
        }

        protected void onPlayerRespawn(Location respawnLoc) {
            List<Entity> list = new ArrayList<>(getGlobalWorldList(getPlayerWorld()));
            //list.removeAll(Collections.singleton(null));

            list.addAll(getGlobalWorldList(getPlayerWorld()));
    
            for (Entity entity : list) {
                if (entity == null) {
                    continue;
                }
    
                if (respawnLoc.distanceSquared(entity.getBukkitEntity().getLocation()) < 65535) {
                    sendDestroyPacket(getPlayer(), entity);
                    sendSpawnPacket(getPlayer(), entity);
                    sendUpdatePacket(getPlayer(), entity, true);
                }
            }
        }

        protected void onPlayerChunkUnload(int chunkX, int chunkZ) {
            List<Entity> list = new ArrayList<>(getGlobalWorldList(getPlayerWorld()));
            //list.removeAll(Collections.singleton(null));

            list.addAll(getGlobalWorldList(getPlayerWorld()));
    
            for (Entity entity : list) {
                if (entity == null) {
                    continue;
                }
    
                if (chunkX == ((int) Math.floor(entity.locX) >> 4)
                        && chunkZ == (int) Math.floor(entity.locZ) >> 4) {
                    sendDestroyPacket(getPlayer(), entity);
                }
            }
        }

    }
}
