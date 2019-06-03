package com.storycraft.server.entity;

import org.bukkit.entity.Player;

public interface IEntityHandler {
    
        /* isCancelled */
    public boolean onSpawnSend(Player p, int eid);
    
        /* isCancelled */
    public boolean onMetadataSend(Player p);

        /* isCancelled */
    public boolean onMoveSend(Player p, short deltaX, short deltaY, short deltaZ, boolean onGround);

        /* isCancelled */
    public boolean onLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround);

        /* isCancelled */
    public boolean onLookAndMove(Player p, short deltaX, short deltaY, short deltaZ, byte yawAngle, byte pitchAngle, boolean onGround);

        /* isCancelled */
    public boolean onTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch, boolean onGround);

    /* destory entity idList */
    public int[] onDestroySend(Player p, int[] eidList);
}