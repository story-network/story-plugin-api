package com.storycraft.core;

import com.storycraft.StoryPlugin;
import com.storycraft.util.Parallel;

import java.util.ArrayList;
import java.util.List;

public class MiniPluginLoader {
    private StoryPlugin plugin;

    private List<IMiniPlugin> miniPluginList;

    public MiniPluginLoader(StoryPlugin plugin){
        this.plugin = plugin;
        this.miniPluginList = new ArrayList<>();
    }

    public void onLoad(){
        Parallel.forEach(this.getMiniPluginList(), new Parallel.Operation<IMiniPlugin>() {
            @Override
            public void run(IMiniPlugin param) {
                param.onLoad(plugin);
            }
        });
    }

    public void onUnload(boolean reload){
        Parallel.forEach(this.getMiniPluginList(), new Parallel.Operation<IMiniPlugin>() {
            @Override
            public void run(IMiniPlugin param) {
                param.onUnload(reload);
            }
        });
    }

    public void addMiniPlugin(IMiniPlugin miniPlugin){
        getMiniPluginList().add(miniPlugin);
    }

    public boolean hasMiniPlugin(IMiniPlugin miniPlugin){
        return getMiniPluginList().contains(miniPlugin);
    }

    public void removeMiniPlugin(IMiniPlugin miniPlugin){
        getMiniPluginList().remove(miniPlugin);
    }

    public StoryPlugin getPlugin(){
        return plugin;
    }

    public List<IMiniPlugin> getMiniPluginList(){
        return miniPluginList;
    }
}
