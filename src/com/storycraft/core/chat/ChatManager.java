package com.storycraft.core.chat;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager extends MiniPlugin implements Listener {

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;

        ServerRank rank = getPlugin().getRankManager().getRank(e.getPlayer());

        e.setFormat("" + ChatColor.BLUE + e.getPlayer().getLevel() + " " + rank.getNameColor() + "%1$s" + " " + ChatColor.WHITE + "%2$s");
    }
}
