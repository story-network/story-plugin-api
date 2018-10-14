package com.storycraft.core.morph;

import com.storycraft.core.MiniPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class MorphManager extends MiniPlugin {

    private Map<World, Map<Integer, AbstractMap.SimpleEntry<Entity, Entity>>> morphWorldMap;
    private MorphHandler handler;

    private Reflect.WrappedField<Integer, PacketPlayOutEntityMetadata> packetMetadataEidField;

    public MorphManager(){
        this.morphWorldMap = new HashMap<>();

        this.handler = new MorphHandler();

        this.packetMetadataEidField = Reflect.getField(PacketPlayOutEntityMetadata.class, "a");
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    public void setMorphToEntity(Entity target, Entity entity){
        World w = target.getBukkitEntity().getWorld();
        Map<Integer, AbstractMap.SimpleEntry<Entity, Entity>> morphMap;

        if (!containsWorldMap(w)) {
            morphWorldMap.put(w, morphMap = new HashMap<>());
        }
        else {
            morphMap = morphWorldMap.get(w);
        }

        AbstractMap.SimpleEntry entry = new AbstractMap.SimpleEntry<>(target, entity);
        if (morphMap.containsKey(target.getId())){
            morphMap.replace(target.getId(), entry);
        }
        else{
            morphMap.put(target.getId(), entry);
        }

        //update again
        ConnectionUtil.sendPacketNearby(target.getBukkitEntity().getLocation(), EntityPacketUtil.getEntitySpawnPacket(target));
    }

    protected boolean containsWorldMap(World w){
        return morphWorldMap.containsKey(w);
    }

    public Entity getMorphEntity(Entity target){
        if (!contains(target))
            return null;

        return getMorphEntityInternal(target.getBukkitEntity().getWorld(), target.getId()).getValue();
    }

    private AbstractMap.SimpleEntry<Entity, Entity> getMorphEntityInternal(World w, int eid){
        return morphWorldMap.get(w).get(eid);
    }

    public void removeMorph(Entity target) {
        if (!contains(target))
            return;

        morphWorldMap.get(target.getBukkitEntity().getWorld()).remove(target);
    }

    public boolean contains(Entity target){
        return containsInternal(target.getBukkitEntity().getWorld(), target.getId());
    }

    private boolean containsInternal(World w, int eid){
        return containsWorldMap(w) && morphWorldMap.get(w).containsKey(eid);
    }

    private class MorphHandler implements Listener {

        @EventHandler
        public void onEntityPacket(AsyncPacketOutEvent e){
            if (!EntityPacketUtil.isEntitySpawnPacket(e.getPacket()))
                return;

            World w = e.getTarget().getWorld();
            if (!containsWorldMap(w))
                return;

            Packet entitySpawnPacket = e.getPacket();
            int eid = EntityPacketUtil.getEntityIdFromPacket(entitySpawnPacket);

            if (!containsInternal(w, eid))
                return;

            AbstractMap.SimpleEntry<Entity, Entity> entry = getMorphEntityInternal(w, eid);

            Entity target = entry.getKey();
            Entity entity = entry.getValue();

            entity.setLocation(target.locX, target.locY, target.locZ, target.yaw, target.pitch);

            if (entity instanceof EntityPlayer){
                sendFakePlayerPacket(e.getTarget(), (EntityPlayer) entity);
            }

            Packet morphPacket = EntityPacketUtil.getEntitySpawnPacket(entity);
            EntityPacketUtil.setEntityIdPacket(morphPacket, eid);

            e.setPacket(morphPacket);
        }

        protected void sendFakePlayerPacket(Player p, EntityPlayer... playerEntitys) {
            PacketPlayOutPlayerInfo infoAddPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, playerEntitys);
            PacketPlayOutPlayerInfo infoRemovePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, playerEntitys);

            ConnectionUtil.sendPacket(p, infoAddPacket);
            getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ConnectionUtil.sendPacket(p, infoRemovePacket);
                }
            }, 1);
        }

        @EventHandler
        public void onMetadataPacket(AsyncPacketOutEvent e){
            if (!(e.getPacket() instanceof PacketPlayOutEntityMetadata))
                return;

            World w = e.getTarget().getWorld();
            if (!containsWorldMap(w))
                return;

            PacketPlayOutEntityMetadata metadataPacket = (PacketPlayOutEntityMetadata) e.getPacket();
            int eid = packetMetadataEidField.get(metadataPacket);

            if (!containsInternal(w, eid))
                return;

            e.setPacket(EntityPacketUtil.getEntityMetadataPacket(getMorphEntityInternal(w, eid).getValue()));
        }
    }
}
