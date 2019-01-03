package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;

import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

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
    public boolean onMorphMetadataSend(Player p) {
		return false;
	}

    @Override
    public void onMorphSpawnSend(Player p, int eid) {

    }

    @Override
    public void onMorphDestroySend(Player p) {

    }

    @Override
    public boolean onMorphMoveSend(Player p, int deltaX, int deltaY, int deltaZ, boolean onGround) {
        return false;
    }

    @Override
    public boolean onMorphLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
        return false;
    }

    @Override
    public boolean onMorphLookAndMove(Player p, int deltaX, int deltaY, int deltaZ, byte yawAngle, byte pitchAngle,
            boolean onGround) {
        return false;
    }

    @Override
    public boolean onMorphTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch,
            boolean onGround) {
        return false;
    }

}