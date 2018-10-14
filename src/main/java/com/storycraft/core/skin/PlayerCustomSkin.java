package com.storycraft.core.skin;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PlayerCustomSkin extends MiniPlugin implements Listener {
    
    @Override
    public void onLoad(StoryPlugin plugin) {

    }

    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void onDisable(boolean reload) {

    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {
        Player p = getPlugin().getServer().getPlayer("storycraft");
        //p.getInventory().addItem(new ItemStack(Material.APPLE));
    }
}
