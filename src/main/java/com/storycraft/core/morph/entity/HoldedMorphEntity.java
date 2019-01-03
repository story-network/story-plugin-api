package com.storycraft.core.morph.entity;

import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityAreaEffectCloud;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntity;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;

public abstract class HoldedMorphEntity implements IMorphEntity {

    private int eid;

    private Entity entity;
    private EntityArmorStand holderEntity;

    public HoldedMorphEntity(Entity entity) {
        this.eid = 0;
        this.entity = entity;
        this.holderEntity = new EntityArmorStand(entity.getWorld(), entity.locX, entity.locY, entity.locZ);

        holderEntity.setSmall(true);
        holderEntity.setNoGravity(true);
        holderEntity.setInvisible(true);
    }

    public EntityArmorStand getHolderEntity() {
        return holderEntity;
    }

    @Override
    public Entity getNMSEntity() {
        return entity;
    }

    @Override
    public void onMorphSpawnSend(Player p, int eid) {
        this.eid = eid;
        getHolderEntity().setLocation(getNMSEntity().locX, getNMSEntity().locY - 0.5f, getNMSEntity().locZ, getNMSEntity().yaw, getNMSEntity().pitch);

        ConnectionUtil.sendPacket(p, PacketUtil.getEntitySpawnPacket(getHolderEntity()));
        ConnectionUtil.sendPacket(p, PacketUtil.getEntityMetadataPacket(getHolderEntity(), true));
        ConnectionUtil.sendPacket(p, PacketUtil.getEntityMountPacket(getHolderEntity(), eid));
    }

    @Override
    public boolean onMorphMetadataSend(Player p) {
        return false;
    }

    @Override
    public void onMorphDestroySend(Player p) {
        ConnectionUtil.sendPacket(p, PacketUtil.getEntityDestroyPacket(getHolderEntity()));
    }

    @Override
    public boolean onMorphMoveSend(Player p, int deltaX, int deltaY, int deltaZ, boolean onGround) {
        ConnectionUtil.sendPacket(p, new PacketPlayOutEntity.PacketPlayOutRelEntityMove(getHolderEntity().getId(), deltaX, deltaY, deltaZ, onGround));
        return true;
    }

    @Override
    public boolean onMorphLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
        ConnectionUtil.sendPacket(p, new PacketPlayOutEntity.PacketPlayOutEntityLook(getHolderEntity().getId(), yawAngle, pitchAngle, onGround));
        return true;
    }

    @Override
    public boolean onMorphLookAndMove(Player p, int deltaX, int deltaY, int deltaZ, byte yawAngle, byte pitchAngle, boolean onGround) {
        ConnectionUtil.sendPacket(p, new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getHolderEntity().getId(), deltaX, deltaY, deltaZ, yawAngle, pitchAngle, onGround));
        return true;
    }

    @Override
    public boolean onMorphTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch, boolean onGround) {
        getHolderEntity().setLocation(locX, locY - 0.5f, locZ, yaw, pitch);
        getHolderEntity().onGround = onGround;
        
        ConnectionUtil.sendPacket(p, new PacketPlayOutEntityTeleport(getHolderEntity()));
        
        return false;
    }

}