package com.storycraft.server.event.client;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class AsyncPlayerChunkEvent extends Event implements Cancellable {

    private Player player;

    private World world;
    private int chunkX, chunkZ;

    private boolean cancelled;

    public AsyncPlayerChunkEvent(Player player, World world, int chunkX, int chunkZ) {
        super(true);
        this.player = player;
        
        this.world = world;

        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.cancelled = false;
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public World getWorld(){
        return world;
    }

    public int getChunkX(){
        return chunkX;
    }

    public int getChunkZ(){
        return chunkZ;
    }
}
