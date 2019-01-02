package com.storycraft.core.player.login;

import java.util.UUID;

import com.storycraft.core.MiniPlugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class ServerKickMessage extends MiniPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID id = e.getUniqueId();

        if (e.getLoginResult() != Result.ALLOWED) {
            switch (e.getLoginResult()) {

                case KICK_BANNED:
                    e.setKickMessage(getPlugin().getServerName() + "\n" + ChatColor.RED + "접속이 제한된 계정입니다.\n" + ChatColor.YELLOW + "사유: " + ChatColor.WHITE + e.getKickMessage() + "\n" + (getPlugin().getServerHomepage().isEmpty() ? "" : ChatColor.WHITE + "자세한 내용은 " + ChatColor.GREEN + getPlugin().getServerHomepage() + ChatColor.WHITE + " 에서 확인할수 있습니다"));
                    break;

                case KICK_WHITELIST:
                    e.setKickMessage(createKickMessage());
                    break;

                case KICK_FULL:
                    e.setKickMessage(getPlugin().getServerName() + "\n" + ChatColor.YELLOW + "서버 접속 가능 인원이 초과되었습니다");
                    break;

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKicked(PlayerKickEvent e) {
        e.setReason(createKickMessage(e.getReason()));
    }

    public String createKickMessage() {
        return getPlugin().getServerName() + "\n" + ChatColor.YELLOW + "접속이 제한되었습니다 .\n" + (getPlugin().getServerHomepage().isEmpty() ? "" : ChatColor.WHITE + "자세한 내용은 " + ChatColor.GREEN + getPlugin().getServerHomepage() + ChatColor.WHITE + " 에서 확인할수 있습니다");
    }

    public String createKickMessage(String reason) {
        return getPlugin().getServerName() + "\n" + ChatColor.YELLOW + "접속이 제한되었습니다 .\n" + ChatColor.YELLOW + "사유: " + ChatColor.WHITE + reason + "\n" + (getPlugin().getServerHomepage().isEmpty() ? "" : ChatColor.WHITE + "자세한 내용은 " + ChatColor.GREEN + getPlugin().getServerHomepage() + ChatColor.WHITE + " 에서 확인할수 있습니다");
    }
}