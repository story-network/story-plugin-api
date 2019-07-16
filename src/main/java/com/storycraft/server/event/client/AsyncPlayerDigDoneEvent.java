package com.storycraft.server.event.client;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPlayerDigDoneEvent extends AsyncPlayerDigEvent {

    private static final HandlerList handlers = new HandlerList();

    public AsyncPlayerDigDoneEvent(Player player, Location targetLocation) {
        super(player, targetLocation);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}