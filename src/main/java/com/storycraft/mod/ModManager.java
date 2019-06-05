package com.storycraft.mod;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.mod.season2.Season2MiniPlugin;

public class ModManager extends MiniPlugin {

    private StoryPlugin plugin;

    public ModManager(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(new Season2MiniPlugin());
    }

    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable(boolean reload) {

    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public MiniPluginLoader getMiniPluginLoader() {
        return getPlugin().getMiniPluginLoader();
    }
}