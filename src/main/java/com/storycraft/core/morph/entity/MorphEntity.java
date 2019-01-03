package com.storycraft.core.morph.entity;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class MorphEntity implements IMorphEntity {

    private Entity entity;
    private DataWatcher metadata;

    public MorphEntity(Entity entity, DataWatcher metadata) {
        this.entity = entity;
        this.metadata = metadata;
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