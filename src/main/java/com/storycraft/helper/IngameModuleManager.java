package com.storycraft.helper;

import com.storycraft.command.ICommand;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.command.CommandSender;

import com.storycraft.MiniPlugin;
import com.storycraft.StoryPlugin;

public class IngameModuleManager extends MiniPlugin implements ICommand {
    
    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "module" };
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.ingamemodule";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "ModuleManager", "사용법 /module <enable/load/disable/remove/reload>"));
            return;
        }

        
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
		return false;
	}
}