package com.storycraft.server.plugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CorePluginUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public CorePluginUpdateEvent() {
        super(true);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}