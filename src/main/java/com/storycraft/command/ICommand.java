package com.storycraft.command;

import org.bukkit.command.CommandSender;

public interface ICommand {

    String[] getAliases();
    void onCommand(CommandSender sender, String[] args);

    boolean availableOnConsole();
    boolean availableOnCommandBlock();

    boolean isPermissionRequired(); 
    
    default String getPermissionRequired() {
        return "";
    }
}
