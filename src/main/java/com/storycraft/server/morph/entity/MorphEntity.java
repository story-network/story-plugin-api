package com.storycraft.server.morph.entity;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;

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