package com.storycraft.mod;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.mod.session2.Session2MiniPlugin;

public class ModManager extends MiniPlugin {

    private StoryPlugin plugin;

    public ModManager(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        MiniPluginLoader loader = getMiniPluginLoader();

        loader.addMiniPlugin(new Session2MiniPlugin());
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