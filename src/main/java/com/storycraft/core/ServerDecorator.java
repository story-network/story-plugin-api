package com.storycraft.core;

import com.storycraft.StoryPlugin;
import com.storycraft.core.advancement.AdvancementManager;
import com.storycraft.core.dropping.HologramXPDrop;
import com.storycraft.core.hologram.HologramManager;
import com.storycraft.core.map.CustomMapManager;
import com.storycraft.core.morph.MorphManager;
import com.storycraft.core.playerlist.ServerPlayerList;

public class ServerDecorator {

    private StoryPlugin plugin;

    private HologramManager hologramManager;
    private AdvancementManager advancementManager;

    private CustomMapManager customMapManager;

    private HologramXPDrop hologramXPDrop;
    private MorphManager morphManager;
    private ServerPlayerList playerList;

    public ServerDecorator(StoryPlugin plugin){
        this.plugin = plugin;

        initialize();
    }

    protected void initialize(){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(hologramManager = new HologramManager());
        loader.addMiniPlugin(advancementManager = new AdvancementManager());
        loader.addMiniPlugin(hologramXPDrop = new HologramXPDrop());
        loader.addMiniPlugin(morphManager = new MorphManager());
        loader.addMiniPlugin(playerList = new ServerPlayerList());
        loader.addMiniPlugin(customMapManager = new CustomMapManager());
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public HologramXPDrop getHologramXPDrop() {
        return hologramXPDrop;
    }

    public MorphManager getMorphManager() {
        return morphManager;
    }
    
    public AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    public CustomMapManager getCustomMapManager() {
        return customMapManager;
    }

    public ServerPlayerList getPlayerList() {
        return playerList;
    }
}
