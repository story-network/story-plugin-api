package com.storycraft.test;

import com.storycraft.StoryPlugin;
import org.bukkit.event.Listener;

public class TestFunction implements Listener {

    private StoryPlugin plugin;

    public TestFunction(StoryPlugin plugin){
        this.plugin = plugin;
    }

    public static void test(StoryPlugin plugin, org.bukkit.World world) {
        plugin.getServer().getPluginManager().registerEvents(new TestFunction(plugin), plugin);
    }

}