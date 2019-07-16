package com.storycraft.effect.player;

import com.storycraft.StoryPlugin;
import com.storycraft.effect.IHasDuration;
import com.storycraft.server.event.server.ServerUpdateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EffectTracker implements Listener {

    private StoryPlugin plugin;

    private IHasDuration target;

    private Runnable onEnd;

    public EffectTracker(StoryPlugin plugin) {
        this.plugin = plugin;
        this.target = null;
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public EffectTracker setOnEndListener(Runnable onEnd) {
        this.onEnd = onEnd;

        return this;
    }

    public boolean isPlaying(long time) {
        if (target == null)
            return false;
        
        return target.getStartTime() + target.getDuration() >= time;
    }

    public IHasDuration getTarget() {
        return target;
    }

    public boolean isTracking() {
        return target != null;
    }

    public EffectTracker track(IHasDuration effect) {
        if (isTracking()) {
            stopTarget();
        }

        this.target = effect;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        return this;
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {
        if (!isPlaying(System.currentTimeMillis())) {
            stopTarget();
        }
    }

    protected void stopTarget() {
        target.stop();
        ServerUpdateEvent.getHandlerList().unregister(this);
        
        if (onEnd != null) {
            onEnd.run();
        }
    }
}