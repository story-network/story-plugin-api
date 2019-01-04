package com.storycraft.core.command;

import com.storycraft.command.ICommand;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.command.CommandSender;

public class SayCommand implements ICommand {

    @Override
    public String[] getAliases() {
        return new String[] { "say" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sender.getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageType.ALERT, sender.getName(), String.join(" ", args)));
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return true;
    }

    @Override
    public boolean isPermissionRequired() {
		return true;
    }
    
    @Override
    public String getPermissionRequired() {
        return "server.command.say";
    }

}