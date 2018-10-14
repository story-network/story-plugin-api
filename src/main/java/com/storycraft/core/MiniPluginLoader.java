package com.storycraft.core;

import com.storycraft.StoryPlugin;
import com.storycraft.util.Parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MiniPluginLoader {
    private StoryPlugin plugin;

    private List<MiniPlugin> miniPluginList;
    private boolean enabled;

    public MiniPluginLoader(StoryPlugin plugin) {
        this.plugin = plugin;
        this.miniPluginList = new ArrayList<>();
        this.enabled = false;
    }

    public void onEnable() {
        setEnabled(true);

        getMiniPluginList().forEach(new Consumer<MiniPlugin>() {
            @Override
            public void accept(MiniPlugin miniPlugin) {
                if (!miniPlugin.isEnabled()) {
                    miniPlugin.onEnable();
                    miniPlugin.setEnabled(true);
                }
            }
        });
    }

    public void onDisable(boolean reload) {
        setEnabled(false);

        getMiniPluginList().forEach(new Consumer<MiniPlugin>() {
            @Override
            public void accept(MiniPlugin miniPlugin) {
                if (miniPlugin.isEnabled()) {
                    miniPlugin.onDisable(reload);
                    miniPlugin.setEnabled(false);
                }
            }
        });
    }

    public void addMiniPlugin(MiniPlugin miniPlugin) {
        getMiniPluginList().add(miniPlugin);

        miniPlugin.onLoad(getPlugin());
        miniPlugin.setPlugin(getPlugin());

        if (isEnabled()){
            miniPlugin.onEnable();
            miniPlugin.setEnabled(true);
        }
    }

    public boolean hasMiniPlugin(MiniPlugin miniPlugin) {
        return getMiniPluginList().contains(miniPlugin);
    }

    public void removeMiniPlugin(MiniPlugin miniPlugin) {
        getMiniPluginList().remove(miniPlugin);
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<MiniPlugin> getMiniPluginList() {
        return miniPluginList;
    }
}
