package com.storycraft.server.event.client;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class AsyncPlayerChunkEvent extends Event implements Cancellable {

    private Player player;
    private Chunk chunk;
    private boolean cancelled;

    public AsyncPlayerChunkEvent(Player player, Chunk chunk) {
        super(true);
        this.player = player;
        this.chunk = chunk;
        this.cancelled = false;
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public World getWorld(){
        return getPlayer().getWorld();
    }

    public Chunk getChunk(){
        return chunk;
    }
}
