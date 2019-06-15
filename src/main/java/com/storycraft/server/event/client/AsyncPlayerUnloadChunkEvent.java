package com.storycraft.server.event.client;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPlayerUnloadChunkEvent extends AsyncPlayerChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public AsyncPlayerUnloadChunkEvent(Player who, World world, int chunkX, int chunkZ) {
        super(who, world, chunkX, chunkZ);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
