package com.storycraft.core.entity.movement;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityToggleCrawlEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private LivingEntity living;
    private boolean isCrawl;

    private boolean cancelled;

    public EntityToggleCrawlEvent(LivingEntity living, boolean isCrawl) {
        super(true);

        this.living = living;
        this.isCrawl = isCrawl;
        this.cancelled = false;
    }
    
    public LivingEntity getEntity() {
        return living;
    }
    
    public boolean isCrawling() {
        return isCrawl;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

}