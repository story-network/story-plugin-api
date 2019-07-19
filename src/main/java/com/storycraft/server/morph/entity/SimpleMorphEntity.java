package com.storycraft.server.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;

import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;

public class SimpleMorphEntity implements IMorphEntity {

    private Entity entity;
    private DataWatcher metadata;

    public SimpleMorphEntity(org.bukkit.entity.Entity entity, EntityType type) {
        this.entity = ((CraftWorld)entity.getWorld()).createEntity(entity.getLocation(), type.getEntityClass());
        this.metadata = new ComparingDataWatcher(((CraftEntity)entity).getHandle(), this.entity);
    }

    @Override
    public Entity getNMSEntity() {
        return entity;
    }

    @Override
	public DataWatcher getFixedMetadata() {
		return metadata;
    }

    @Override
    public boolean onSpawnSend(Player p, int eid) {
        return false;
    }

    @Override
    public boolean onMetadataSend(Player p) {
        return false;
    }

    @Override
    public boolean onMoveSend(Player p, short deltaX, short deltaY, short deltaZ, boolean onGround) {
        return false;
    }

    @Override
    public boolean onLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
        return false;
    }

    @Override
    public boolean onLookAndMove(Player p, short deltaX, short deltaY, short deltaZ, byte yawAngle, byte pitchAngle,
            boolean onGround) {
        return false;
    }

    @Override
    public boolean onTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch,
            boolean onGround) {
        return false;
    }

    @Override
    public int[] onDestroySend(Player p, int[] eidList) {
        return null;
    }

}