package com.storycraft.server.event.client;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPlayerLoadChunkEvent extends AsyncPlayerChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean isFullChunk;

    public AsyncPlayerLoadChunkEvent(Player who, World world, int chunkX, int chunkZ, boolean isFullChunk) {
        super(who, world, chunkX, chunkZ);
        this.isFullChunk = isFullChunk;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isFullChunk() {
        return isFullChunk;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
