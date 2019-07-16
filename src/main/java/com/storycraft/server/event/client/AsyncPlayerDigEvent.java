package com.storycraft.server.event.client;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AsyncPlayerDigEvent extends Event implements Cancellable {

    private Player player;
    private Location targetLocation;

    private boolean cancelled;

    public AsyncPlayerDigEvent(Player player, Location targetLocation) {
        super(true);
        this.player = player;
        
        this.targetLocation = targetLocation;

        this.cancelled = false;
    }

    public Player getPlayer(){
        return player;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}