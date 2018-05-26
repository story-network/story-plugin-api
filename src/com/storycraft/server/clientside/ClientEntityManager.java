package com.storycraft.server.clientside;

import com.storycraft.server.ServerExtension;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.server.update.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
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

    public void update(Entity e){
        sendUpdatePacket(e);
    }

    protected void sendSpawnPacket(Player p, Entity e){
        ConnectionUtil.sendPacket(p, EntityPacketUtil.getEntitySpawnPacket(e));
    }

    protected void sendSpawnPacket(Entity e){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, EntityPacketUtil.getEntitySpawnPacket(e));
    }

    protected void sendUpdatePacket(Entity e){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, EntityPacketUtil.getEntityMetadataPacket(e));
    }

    protected void sendUpdatePacket(Player p, Entity e){
        ConnectionUtil.sendPacket(p, EntityPacketUtil.getEntityMetadataPacket(e));
    }

    protected void sendDestroyPacket(Entity e){
        Location loc = e.getBukkitEntity().getLocation();

        ConnectionUtil.sendPacketNearby(loc, EntityPacketUtil.getEntityDestroyPacket(e));
    }

    protected void sendDestroyPacket(Player p, Entity e){
        ConnectionUtil.sendPacket(p, EntityPacketUtil.getEntityDestroyPacket(e));
    }

    @EventHandler
    public void onPlayerChunkLoad(AsyncPacketOutEvent e){
        if (e.getPacket() instanceof PacketPlayOutMapChunk){
            Player p = e.getTarget();

            World w = ((CraftWorld)p.getWorld()).getHandle();

            if (!hasWorld(w))
                return;

            int locX = Reflect.getField(e.getPacket(), "a");
            int locZ = Reflect.getField(e.getPacket(), "b");

            for (Entity entity : new ArrayList<Entity>(entityMap.get(w))){
                Chunk chunk = entity.getBukkitEntity().getLocation().getChunk();
                if (chunk.getX() == locX && chunk.getZ() == locZ) {
                    sendSpawnPacket(p, entity);
                    sendUpdatePacket(p, entity);
                }
            }
        }
    }
}
