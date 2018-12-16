package com.storycraft.core.uuid;

import java.io.IOException;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MojangAPI;
import com.storycraft.util.AsyncTask.AsyncCallable;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UUIDRevealCommand extends MiniPlugin implements ICommand {

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "uuid" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "UUID", "사용법 /uuid <플레이어 이름>"));
            return;
        }

        new AsyncTask<Void>(new AsyncCallable<Void>() {
            @Override
            public Void get() {
                OfflinePlayer p = null;

                if (args.length > 0) {
                    p = getPlugin().getServer().getOfflinePlayer(args[0]);
        
                    if (p == null) {
                        sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "UUID", "플레이어 " + args[0] + " 을 찾을 수 없습니다"));
                        return null;
                    }
                }
                else {
                    p = (Player) sender;
                }

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "UUID", "플레이어 " + args[0] + " 의 서버 UUID: " + ChatColor.YELLOW + p.getUniqueId().toString()));
                try {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "UUID", "플레이어 " + args[0] + " 의 계정 UUID: " + ChatColor.YELLOW + MojangAPI.getSessionPlayerUUID(p.getName())));
                } catch (IOException e) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "UUID", "플레이어 " + args[0] + " 의 계정 UUID를 불러 올 수 없습니다"));
                }

                return null;
            }
        }).run();
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return false;
    }

    @Override
    public boolean isPermissionRequired() {
		return false;
	}

}