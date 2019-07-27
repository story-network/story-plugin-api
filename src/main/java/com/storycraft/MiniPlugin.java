package com.storycraft;

import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class MiniPlugin {
    
    private MainPlugin plugin;
    private boolean enabled = false;

    public void onLoad(MainPlugin plugin) {

    }

    public void onEnable() {

    }

    public void onDisable(boolean reload) {

    }

    public MainPlugin getPlugin() {
        return plugin;
    }

    protected void setPlugin(MainPlugin plugin) {
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

    public <T>Future<T> runSync(Callable<T> callable){
        return getPlugin().getServer().getScheduler().callSyncMethod(getPlugin(), callable);
    }
}
