package com.storycraft.core.rank;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class RankUpdateEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private ServerRank from;
    private ServerRank to;

    public RankUpdateEvent(Player p, ServerRank from, ServerRank to) {
        super(p);

        this.from = from;
        this.to = to;
    }

    public ServerRank getFrom() {
        return from;
    }
    
    public ServerRank getTo() {
        return to;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}