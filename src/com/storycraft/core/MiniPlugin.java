package com.storycraft.core;

import com.storycraft.StoryPlugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class MiniPlugin {
    private StoryPlugin plugin;
    private boolean enabled = false;

    public void onLoad(StoryPlugin plugin) {

    }

    public void onEnable() {

    }

    public void onDisable(boolean reload) {

    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    protected void setPlugin(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMainThread(){
        return getPlugin().getServer().isPrimaryThread();
    }

    public BukkitTask runSync(Runnable runnable){
        return getPlugin().getServer().getScheduler().runTask(getPlugin(), runnable);
    }
}
