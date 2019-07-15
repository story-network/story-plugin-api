package com.storycraft.core.motd;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.MiniPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMotd extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("motd.json", configFile = new JsonConfigFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e){
        e.setMotd(getMotd());
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

    public String getMotd() {
        if (configFile.contains("motd")) {
            try {
                return configFile.get("motd").getAsString();
            } catch (Exception e) {
                e.printStackTrace();
                String defaultMotd = getPlugin().getServerName();
                setMotd(defaultMotd);

                return getMotd();
            }
        }
        else {
            String defaultMotd = getPlugin().getServerName();
            setMotd(defaultMotd);

            return defaultMotd;
        }
    }

    public void setMotd(String string) {
        configFile.set("motd", string);
    }
}
