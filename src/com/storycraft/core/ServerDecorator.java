package com.storycraft.core;

import com.storycraft.StoryPlugin;
import com.storycraft.core.hologram.HologramManager;

public class ServerDecorator {

    private StoryPlugin plugin;

    private HologramManager hologramManager;

    public ServerDecorator(StoryPlugin plugin){
        this.plugin = plugin;

        initialize();
    }

    protected void initialize(){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(hologramManager = new HologramManager());
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
