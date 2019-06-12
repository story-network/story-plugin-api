package com.storycraft.server.clientside;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.event.client.AsyncPlayerLoadChunkEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientEntityManager extends ServerExtension implements Listener {

    private Map<World, List<Entity>> entityMap;

    public ClientEntityManager(){
        this.entityMap = new HashMap<>();
    }

    @Override
    public void onDisable(boolean reload){
        for (World w : entityMap.keySet()){
            for (Entity e : entityMap.get(w)){
                sendDestroyPacket(e);
            }
        }

        entityMap.clear();
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void addClientEntity(Entity e){
        if (contains(e))
            return;

        if (!hasWorld(e.getWorld())) {
            entityMap.put(e.getWorld(), new ArrayList<>());
        }

        entityMap.get(e.getWorld()).add(e);
        sendSpawnPacket(e);
        sendUpdatePacket(e);
    }

    public void removeClientEntity(Entity e){
        if (!contains(e))
            return;

        entityMap.get(e.getWorld()).remove(e);
        sendDestroyPacket(e);
    }

    public boolean contains(Entity e){
        return hasWorld(e.getWorld()) && entityMap.get(e.getWorld()).contains(e);
    }

    protected boolean hasWorld(World w){
        return entityMap.containsKey(w);
    }

    protected List<Entity> getWorldList(World w) {
        List<Entity> list;

        if (!entityMap.containsKey(w)) {
            entityMap.put(w, list = new ArrayList<>());
        }
        else {
            list = entityMap.get(w);
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
    public void onPlayerChunkLoad(AsyncPlayerLoadChunkEvent e) {
        for (Entity entity : new ArrayList<>(getWorldList(((CraftWorld) e.getWorld()).getHandle()))) {
            if (e.getChunk().getX() == ((int) Math.floor(entity.locX) >> 4) && e.getChunk().getZ() == (int)Math.floor(entity.locZ) >> 4) {
				sendDestroyPacket(e.getPlayer(), entity);
                sendSpawnPacket(e.getPlayer(), entity);
                sendUpdatePacket(e.getPlayer(), entity, true);
            }
        }

    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent e) {
        for (Entity entity : new ArrayList<>(getWorldList(((CraftWorld) e.getRespawnedLocation().getWorld()).getHandle()))) {
            if (entity.getBukkitEntity().getLocation().distanceSquared(e.getRespawnedLocation()) < 65535) {
				sendDestroyPacket(e.getPlayer(), entity);
                sendSpawnPacket(e.getPlayer(), entity);
                sendUpdatePacket(e.getPlayer(), entity, true);
            }
        }
    }
}
