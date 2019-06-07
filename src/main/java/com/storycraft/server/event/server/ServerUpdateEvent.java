package com.storycraft.server.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private long currentTick;

    public ServerUpdateEvent(long currentTick){
        super(true);
        this.currentTick = currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public boolean isUpdateType(UpdateType type) {
        return getCurrentTick() % type.getCount() == 0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum UpdateType {
        TICK(0),
        SECOND(20),
        MINUTE(1200),
        HOUR(72000);

        private int count;

        UpdateType(int count){
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }
}
