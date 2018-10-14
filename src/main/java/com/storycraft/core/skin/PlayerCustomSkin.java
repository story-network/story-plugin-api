package com.storycraft.core.skin;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;

import org.bukkit.event.Listener;

public class PlayerCustomSkin extends MiniPlugin implements Listener {
    
    @Override
    public void onLoad(StoryPlugin plugin) {

    }

    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void onDisable(boolean reload) {

    }
}
