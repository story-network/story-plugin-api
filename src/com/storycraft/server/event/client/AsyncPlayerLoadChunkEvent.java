package com.storycraft.server.event.client;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPlayerLoadChunkEvent extends AsyncPlayerChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public AsyncPlayerLoadChunkEvent(Player who, Chunk chunk) {
        super(who, chunk);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
