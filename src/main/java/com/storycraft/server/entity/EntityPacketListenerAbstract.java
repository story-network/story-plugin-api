package com.storycraft.server.entity;

import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.PacketUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntity;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;

public abstract class EntityPacketListenerAbstract implements Listener {

    private static Reflect.WrappedField<Integer, PacketPlayOutEntityMetadata> packetMetadataEidField;

    private static Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityEidField;

    private static Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelXField;
    private static Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelYField;
    private static Reflect.WrappedField<Integer, PacketPlayOutEntity> packetEntityRelZField;

    private static Reflect.WrappedField<Integer, PacketPlayOutEntityTeleport> packetTeleportEidField;

    static {
        packetMetadataEidField = Reflect.getField(PacketPlayOutEntityMetadata.class, "a");

        packetEntityEidField = Reflect.getField(PacketPlayOutEntity.class, "a");
        packetEntityRelXField = Reflect.getField(PacketPlayOutEntity.class, "b");
        packetEntityRelYField = Reflect.getField(PacketPlayOutEntity.class, "c");
        packetEntityRelZField = Reflect.getField(PacketPlayOutEntity.class, "d");

        packetTeleportEidField = Reflect.getField(PacketPlayOutEntityTeleport.class, "a");
    }

    public abstract Entity getEntity();

    public abstract IEntityHandler getHandler();

    public net.minecraft.server.v1_13_R2.Entity getNMSEntity() {
        return ((CraftEntity) getEntity()).getHandle();
    }

    @EventHandler
    public void entitySpawnPacket(AsyncPacketOutEvent e){
        if (!PacketUtil.isEntitySpawnPacket(e.getPacket()))
            return;

        Packet entitySpawnPacket = e.getPacket();
        int eid = PacketUtil.getEntityIdFromPacket(entitySpawnPacket);

        if (eid != getEntity().getEntityId())
            return;

        onEntitySpawnPacket(e, eid);
    }

    @EventHandler
    public void metadataPacket(AsyncPacketOutEvent e) {
        if (!(e.getPacket() instanceof PacketPlayOutEntityMetadata))
            return;

        PacketPlayOutEntityMetadata metadataPacket = (PacketPlayOutEntityMetadata) e.getPacket();

        int eid = packetMetadataEidField.get(metadataPacket);

        if (eid != getEntity().getEntityId())
            return;

        onEntityMetadataPacket(e);
    }

    @EventHandler
    public void destroyPacket(AsyncPacketOutEvent e){
        if (!(e.getPacket() instanceof PacketPlayOutEntityDestroy))
            return;

        PacketPlayOutEntityDestroy destroy = (PacketPlayOutEntityDestroy) e.getPacket();

        int[] idList = PacketUtil.getEntityDestroyList(destroy);

        for (int id : idList) {

            if (id == getEntity().getEntityId()) {
                onEntityDestoryPacket(e, idList);
            }
        }
    }

    @EventHandler
    public void teleportPacket(AsyncPacketOutEvent e){
        if (!(e.getPacket() instanceof PacketPlayOutEntityTeleport))
            return;

        PacketPlayOutEntityTeleport teleport = (PacketPlayOutEntityTeleport) e.getPacket();
        int eid = packetTeleportEidField.get(teleport);

        if (eid == getEntity().getEntityId()) {
            onTeleportPacket(e);
        }
    }

    @EventHandler
    public void entityPacket(AsyncPacketOutEvent e){
        if (!(e.getPacket() instanceof PacketPlayOutEntity))
            return;

        PacketPlayOutEntity entityPacket = (PacketPlayOutEntity) e.getPacket();
        int eid = packetEntityEidField.get(entityPacket);

        if (eid == getEntity().getEntityId()) {
            Location loc = getEntity().getLocation();

            if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutEntityLook) {
                onEntityLookPacket(e);
            }
            else if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove) {
                onEntityMovePacket(e);
            }
            else if (entityPacket instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                onEntityLookAndMovePacket(e);
            }
        }
    }

    protected void onEntitySpawnPacket(AsyncPacketOutEvent e, int eid) {
        e.setCancelled(getHandler().onSpawnSend(e.getTarget(), eid));
    }

    protected void onEntityDestoryPacket(AsyncPacketOutEvent e, int[] idList) {
        int[] proceedIdList = getHandler().onDestroySend(e.getTarget(), idList);

        if (proceedIdList != idList)
            e.setPacket(new PacketPlayOutEntityDestroy(proceedIdList));
    }

    protected void onEntityMetadataPacket(AsyncPacketOutEvent e) {
        e.setCancelled(getHandler().onMetadataSend(e.getTarget()));
    }

    protected void onEntityLookPacket(AsyncPacketOutEvent e) {
        Location loc = getEntity().getLocation();

        e.setCancelled(getHandler().onLookSend(e.getTarget(), (byte) Math.floor(loc.getYaw() * 256f / 360f), (byte) Math.floor(loc.getPitch() * 256f / 360f), getEntity().isOnGround()));
    }

    protected void onEntityMovePacket(AsyncPacketOutEvent e) {
        Location loc = getEntity().getLocation();

        PacketPlayOutEntity.PacketPlayOutRelEntityMove entityPacket = (PacketPlayOutEntity.PacketPlayOutRelEntityMove) e.getPacket();

        e.setCancelled(getHandler().onMoveSend(e.getTarget(), packetEntityRelXField.get(entityPacket), packetEntityRelYField.get(entityPacket), packetEntityRelZField.get(entityPacket), getEntity().isOnGround()));
    }

    protected void onEntityLookAndMovePacket(AsyncPacketOutEvent e) {
        Location loc = getEntity().getLocation();

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook entityPacket = (PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) e.getPacket();

        e.setCancelled(getHandler().onLookAndMove(e.getTarget(), packetEntityRelXField.get(entityPacket), packetEntityRelYField.get(entityPacket), packetEntityRelZField.get(entityPacket), (byte) Math.floor(loc.getYaw() * 256f / 360f), (byte) Math.floor(loc.getPitch() * 256f / 360f), getEntity().isOnGround()));
    }

    protected void onTeleportPacket(AsyncPacketOutEvent e) {
        Location loc = getEntity().getLocation();

        e.setCancelled(getHandler().onTeleportSend(e.getTarget(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), getEntity().isOnGround()));
    }
}