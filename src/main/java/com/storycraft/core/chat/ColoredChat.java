package com.storycraft.core.chat;

import com.storycraft.core.MiniPlugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ColoredChat extends MiniPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("player.chat.color")) {
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        }
    }
    
}