package com.storycraft.server.tick;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerAsyncTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private long currentTick;

    public ServerAsyncTickEvent(long currentTick){
        super(true);
        this.currentTick = currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
