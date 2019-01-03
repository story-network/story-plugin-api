package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.CustomNameDataWatcher;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class NamedMorphEntity implements IMorphEntity {

    private IMorphEntity entity;

    private CustomNameDataWatcher watcher;

    public NamedMorphEntity(IMorphEntity entity, boolean customNameVisible, String customName) {
        this.entity = entity;
        this.watcher = new CustomNameDataWatcher(watcher, customNameVisible, customName);
    }

	@Override
	public Entity getNMSEntity() {
		return entity.getNMSEntity();
	}

	@Override
	public DataWatcher getFixedMetadata() {
		return watcher;
	}

	@Override
	public void onMorphSpawnSend(Player p, int eid) {
		entity.onMorphSpawnSend(p, eid);
	}

	@Override
	public boolean onMorphMetadataSend(Player p) {
		return entity.onMorphMetadataSend(p);
	}

	@Override
	public boolean onMorphMoveSend(Player p, int deltaX, int deltaY, int deltaZ, boolean onGround) {
		return entity.onMorphMoveSend(p, deltaX, deltaY, deltaZ, onGround);
	}

	@Override
	public boolean onMorphLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
		return entity.onMorphLookSend(p, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onMorphLookAndMove(Player p, int deltaX, int deltaY, int deltaZ, byte yawAngle, byte pitchAngle,
			boolean onGround) {
		return entity.onMorphLookAndMove(p, deltaX, deltaY, deltaZ, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onMorphTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch,
			boolean onGround) {
		return entity.onMorphTeleportSend(p, locX, locY, locZ, yaw, pitch, onGround);
	}

	@Override
	public void onMorphDestroySend(Player p) {
		entity.onMorphDestroySend(p);
	}
}