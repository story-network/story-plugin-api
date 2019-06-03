package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.CustomNameDataWatcher;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;

public class NamedMorphEntity implements IMorphEntity {

    private IMorphEntity entity;

    private CustomNameDataWatcher watcher;

    public NamedMorphEntity(IMorphEntity entity, boolean customNameVisible, String customName) {
        this.entity = entity;
        this.watcher = new CustomNameDataWatcher(entity.getFixedMetadata(), customNameVisible, customName);
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
	public boolean onSpawnSend(Player p, int eid) {
		return entity.onSpawnSend(p, eid);
	}

	@Override
	public boolean onMetadataSend(Player p) {
		return entity.onMetadataSend(p);
	}

	@Override
	public boolean onMoveSend(Player p, short deltaX, short deltaY, short deltaZ, boolean onGround) {
		return entity.onMoveSend(p, deltaX, deltaY, deltaZ, onGround);
	}

	@Override
	public boolean onLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
		return entity.onLookSend(p, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onLookAndMove(Player p, short deltaX, short deltaY, short deltaZ, byte yawAngle, byte pitchAngle,
			boolean onGround) {
		return entity.onLookAndMove(p, deltaX, deltaY, deltaZ, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch,
			boolean onGround) {
		return entity.onTeleportSend(p, locX, locY, locZ, yaw, pitch, onGround);
	}

	@Override
	public int[] onDestroySend(Player p, int[] eidList) {
		return entity.onDestroySend(p, eidList);
	}
}