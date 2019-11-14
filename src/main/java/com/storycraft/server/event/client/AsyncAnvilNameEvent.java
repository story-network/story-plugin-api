package com.storycraft.server.event.client;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncAnvilNameEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private String name;

    private boolean cancelled;

    public AsyncAnvilNameEvent(Player player, String name) {
        super(true);
        this.player = player;
    
        this.name = name;
        this.cancelled = false;
    }

    public Player getPlayer(){
        return player;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
