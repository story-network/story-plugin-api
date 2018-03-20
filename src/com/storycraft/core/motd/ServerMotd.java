package com.storycraft.core.motd;

import com.storycraft.core.MiniPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerMotd extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e){
        e.setMotd(getPlugin().getServerName());
    }
}
