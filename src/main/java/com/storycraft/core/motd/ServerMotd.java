package com.storycraft.core.motd;

import com.storycraft.core.MiniPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMotd extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e){
        e.setMotd(getPlugin().getServerName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        e.setJoinMessage(ChatColor.BLUE + " + " + ChatColor.RESET + e.getPlayer().getName());

        e.getPlayer().sendTitle(getPlugin().getServerName(), new SimpleDateFormat().format(new Date()), 10, 30, 10);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        e.setQuitMessage(ChatColor.RED + " - " + ChatColor.RESET + e.getPlayer().getName());
    }
}
