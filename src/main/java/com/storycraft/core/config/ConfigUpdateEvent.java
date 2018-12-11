package com.storycraft.core.config;

import com.storycraft.config.IConfigFile;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConfigUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String name;
    private IConfigFile config;

    public ConfigUpdateEvent(String name, IConfigFile config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }
    
    public IConfigFile getConfig() {
        return config;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}