package com.storycraft;

import com.storycraft.server.advancement.AdvancementManager;
import com.storycraft.server.hologram.HologramManager;
import com.storycraft.server.map.CustomMapManager;
import com.storycraft.server.morph.MorphManager;

public class ServerDecorator {

    private StoryPlugin plugin;

    private HologramManager hologramManager;
    private AdvancementManager advancementManager;

    private CustomMapManager customMapManager;

    private MorphManager morphManager;

    public ServerDecorator(StoryPlugin plugin){
        this.plugin = plugin;

        initialize();
    }

    protected void initialize(){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(hologramManager = new HologramManager());
        loader.addMiniPlugin(advancementManager = new AdvancementManager());
        loader.addMiniPlugin(morphManager = new MorphManager());
        loader.addMiniPlugin(customMapManager = new CustomMapManager());
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
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
}
