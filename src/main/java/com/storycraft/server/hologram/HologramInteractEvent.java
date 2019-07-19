package com.storycraft.server.hologram;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HologramInteractEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Hologram hologram;

    public HologramInteractEvent(Player player, Hologram hologram) {
        super(true);

        this.player = player;
        this.hologram = hologram;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}