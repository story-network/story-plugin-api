package com.storycraft.test;

import com.storycraft.StoryPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestFunction implements Listener {

    private StoryPlugin plugin;

    public TestFunction(StoryPlugin plugin){
        this.plugin = plugin;
    }

    public static void test(StoryPlugin plugin, org.bukkit.World world) {
        plugin.getServer().getPluginManager().registerEvents(new TestFunction(plugin), plugin);
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {

    }

}