package com.storycraft.core;

import com.storycraft.StoryPlugin;
import com.storycraft.core.hologram.HologramManager;
import com.storycraft.core.playerlist.ServerPlayerList;

public class ServerDecorator {

    private StoryPlugin plugin;

    private HologramManager hologramManager;
    private ServerPlayerList playerList;

    public ServerDecorator(StoryPlugin plugin){
        this.plugin = plugin;

        initialize();
    }

    protected void initialize(){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(hologramManager = new HologramManager());
        loader.addMiniPlugin(playerList = new ServerPlayerList());
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ServerPlayerList getPlayerList() {
        return playerList;
    }
}
