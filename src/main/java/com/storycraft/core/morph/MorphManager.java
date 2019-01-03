package com.storycraft.core.morph;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.morph.entity.IMorphEntity;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import net.minecraft.server.v1_13_R2.DataWatcher.Item;

import net.minecraft.server.v1_13_R2.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class MorphManager extends MiniPlugin {

    private List<MorphInfo> morphList;
    private MorphHandler handler;

    private Reflect.WrappedField<Integer, PacketPlayOutEntityMetadata> packetMetadataEidField;

    private Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityEidField;

    private Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelXField;
    private Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelYField;
    private Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelZField;

    private Reflect.WrappedField<Integer, PacketPlayOutEntityTeleport> packetTeleportEidField;

    private WrappedField<List<Item<?>>, PacketPlayOutEntityMetadata> itemListField;

    public MorphManager(){
        this.morphList = new ArrayList<>();

        this.handler = new MorphHandler();

        this.packetMetadataEidField = Reflect.getField(PacketPlayOutEntityMetadata.class, "a");

        this.packetEntityEidField = Reflect.getField(PacketPlayOutEntity.class, "a");
        this.packetEntityRelXField = Reflect.getField(PacketPlayOutEntity.class, "b");
        this.packetEntityRelYField = Reflect.getField(PacketPlayOutEntity.class, "c");
        this.packetEntityRelZField = Reflect.getField(PacketPlayOutEntity.class, "d");

        this.packetTeleportEidField = Reflect.getField(PacketPlayOutEntityTeleport.class, "a");

        this.itemListField = Reflect.getField(PacketPlayOutEntityMetadata.class, "b");
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    public void setMorph(Entity e, IMorphEntity morph) {
        setMorph(new MorphInfo(e, morph));
    }

    public void setMorph(MorphInfo info){
        removeMorph(info.getEntity());

        morphList.add(info);

        //update again
        Packet spawnPacket = PacketUtil.getEntitySpawnPacket(info.getMorph().getNMSEntity());
        
        PacketUtil.setEntityIdPacket(spawnPacket, info.getEntity().getEntityId());

        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), spawnPacket);
        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), PacketUtil.getEntityMetadataPacket(((CraftEntity)info.getEntity()).getHandle(), true));
    }

    public boolean containsEntity(Entity e){
        return getMorphInfo(e) != null;
    }

    private boolean containsEntityInternal(World w, int eid){
        return getMorphInfoInternal(w, eid) != null;
    }

    public MorphInfo getMorphInfo(Entity e){
        return getMorphInfoInternal(e.getWorld(), e.getEntityId());
    }

    private MorphInfo getMorphInfoInternal(World w, int eid){
        for (MorphInfo info : morphList) {
            if (info.getEntity().getWorld().getName().equals(w.getName()) && info.getEntity().getEntityId() == eid) {
                return info;
            }
        }

        return null;
    }

    public void removeMorph(Entity e) {
        MorphInfo info = getMorphInfo(e);

        if (info != null) {
            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), e, PacketUtil.getEntityDestroyPacket(((CraftEntity)e).getHandle()));
            morphList.remove(info);

            //update
            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), e, PacketUtil.getEntitySpawnPacket(((CraftEntity)e).getHandle()));
            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), e, PacketUtil.getEntityMetadataPacket(((CraftEntity)e).getHandle(), true));
        }
    }

    private class MorphHandler implements Listener {

        @EventHandler
        public void onEntityPacket(AsyncPacketOutEvent e){
            if (!PacketUtil.isEntitySpawnPacket(e.getPacket()))
                return;

            World w = e.getTarget().getWorld();
            Packet entitySpawnPacket = e.getPacket();
            int eid = PacketUtil.getEntityIdFromPacket(entitySpawnPacket);

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info == null)
                return;

            Entity entity = info.getEntity();
            IMorphEntity morph = info.getMorph();

            Location loc = entity.getLocation();

            morph.getNMSEntity().setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

            if (morph.getNMSEntity() instanceof EntityPlayer){
                sendFakePlayerPacket(e.getTarget(), (EntityPlayer) entity);
            }

            Packet morphPacket = PacketUtil.getEntitySpawnPacket(morph.getNMSEntity());
            PacketUtil.setEntityIdPacket(morphPacket, eid);

            e.setPacket(morphPacket);

            runSync(() -> {
                info.getMorph().onMorphSpawnSend(e.getTarget(), eid);
                return null;
            });
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
        public void onMetadataPacket(AsyncPacketOutEvent e) {
            if (!(e.getPacket() instanceof PacketPlayOutEntityMetadata))
                return;

            World w = e.getTarget().getWorld();
            PacketPlayOutEntityMetadata metadataPacket = (PacketPlayOutEntityMetadata) e.getPacket();

            int eid = packetMetadataEidField.get(metadataPacket);

            if (eid == e.getTarget().getEntityId())
                return;

            List<Item<?>> itemList = itemListField.get(metadataPacket);

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info == null)
                return;

            //for reupdate
            if (itemList != null) {
                for (Item<?> item : itemList) {
                    item.a(true);
                }
            }

            e.setPacket(PacketUtil.getEntityMetadataPacket(eid, info.getMorph().getFixedMetadata(), true));
            e.setCancelled(info.getMorph().onMorphMetadataSend(e.getTarget()));
        }

        @EventHandler
        public void onDestroy(AsyncPacketOutEvent e){
            if (!(e.getPacket() instanceof PacketPlayOutEntityDestroy))
                return;

            PacketPlayOutEntityDestroy destroy = (PacketPlayOutEntityDestroy) e.getPacket();
            World w = e.getTarget().getWorld();

            for (int id : PacketUtil.getEntityDestroyList(destroy)) {
                MorphInfo info = getMorphInfoInternal(w, id);

                if (info != null) {
                    info.getMorph().onMorphDestroySend(e.getTarget());
                }
            }
        }

        @EventHandler
        public void onTeleport(AsyncPacketOutEvent e){
            if (!(e.getPacket() instanceof PacketPlayOutEntityTeleport))
                return;

            PacketPlayOutEntityTeleport teleport = (PacketPlayOutEntityTeleport) e.getPacket();
            int eid = packetTeleportEidField.get(teleport);
            World w = e.getTarget().getWorld();

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info != null) {
                Entity target = info.getEntity();
                Location loc = target.getLocation();

                e.setCancelled(info.getMorph().onMorphTeleportSend(e.getTarget(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), target.isOnGround()));
            }
        }

        @EventHandler
        public void onEntity(AsyncPacketOutEvent e){
            if (!(e.getPacket() instanceof PacketPlayOutEntity))
                return;

            PacketPlayOutEntity entityPacket = (PacketPlayOutEntity) e.getPacket();
            int eid = packetEntityEidField.get(entityPacket);
            World w = e.getTarget().getWorld();

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info != null) {
                IMorphEntity morphEntity = info.getMorph();
                Entity target = info.getEntity();
                Location loc = target.getLocation();

                if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutEntityLook) {
                    e.setCancelled(morphEntity.onMorphLookSend(e.getTarget(), (byte) Math.floor(loc.getYaw() * 256f / 360f), (byte) Math.floor(loc.getPitch() * 256f / 360f), target.isOnGround()));
                }
                else if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove) {
                    e.setCancelled(morphEntity.onMorphMoveSend(e.getTarget(), packetEntityRelXField.get(entityPacket), packetEntityRelYField.get(entityPacket), packetEntityRelZField.get(entityPacket), target.isOnGround()));
                }
                else if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                    e.setCancelled(morphEntity.onMorphLookAndMove(e.getTarget(), packetEntityRelXField.get(entityPacket), packetEntityRelYField.get(entityPacket), packetEntityRelZField.get(entityPacket), (byte) Math.floor(loc.getYaw() * 256f / 360f), (byte) Math.floor(loc.getPitch() * 256f / 360f), target.isOnGround()));
                }
            }
        }
    }
}
