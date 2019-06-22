package com.storycraft.server.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerSyncUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private long currentTick;

    public ServerSyncUpdateEvent(long currentTick){
        super();
        this.currentTick = currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public boolean isUpdateType(ServerUpdateEvent.UpdateType type) {
        return getCurrentTick() % type.getCount() == 0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
