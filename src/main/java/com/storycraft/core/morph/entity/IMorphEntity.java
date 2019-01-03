package com.storycraft.core.morph.entity;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public interface IMorphEntity {

    public Entity getNMSEntity();
    
    public DataWatcher getFixedMetadata();

    public void onMorphSpawnSend(Player p, int eid);
    
        /* isCancelled */
    public boolean onMorphMetadataSend(Player p);

        /* isCancelled */
    public boolean onMorphMoveSend(Player p, int deltaX, int deltaY, int deltaZ, boolean onGround);

        /* isCancelled */
    public boolean onMorphLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround);

        /* isCancelled */
    public boolean onMorphLookAndMove(Player p, int deltaX, int deltaY, int deltaZ, byte yawAngle, byte pitchAngle, boolean onGround);

        /* isCancelled */
    public boolean onMorphTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch, boolean onGround);

    public void onMorphDestroySend(Player p);
}