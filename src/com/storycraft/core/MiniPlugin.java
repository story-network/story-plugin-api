package com.storycraft.core;

import com.storycraft.StoryPlugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class IMiniPlugin {
    protected StoryPlugin plugin;

    public void onLoad(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {

    }

    public void onUnload(boolean reload) {

    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public boolean isMainThread(){
        return getPlugin().getServer().isPrimaryThread();
    }

    public BukkitTask runSync(Runnable runnable){
        return getPlugin().getServer().getScheduler().runTask(getPlugin(), runnable);
    }
}
