package com.storycraft;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class MiniPlugin<T extends MainPlugin> {
    
    private T plugin;
    private boolean enabled = false;

    public void onLoad(T plugin) {

    }

    public void onEnable() {

    }

    public void onDisable(boolean reload) {

    }

    public T getPlugin() {
        return plugin;
    }

    protected void setPlugin(T plugin) {
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

    public <S>Future<S> runSync(Callable<S> callable){
        return getPlugin().getServer().getScheduler().callSyncMethod(getPlugin(), callable);
    }
}
