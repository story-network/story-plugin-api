package com.storycraft.server.event.client;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPlayerBrandSentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private String brand;

    public AsyncPlayerBrandSentEvent(Player player, String brand) {
        super(true);
        this.player = player;
    
        this.brand = brand;
    }

    public Player getPlayer(){
        return player;
    }

    public String getBrand() {
        return brand;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
