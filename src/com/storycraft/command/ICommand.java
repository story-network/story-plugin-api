package com.storycraft.command;

import org.bukkit.entity.Player;

public interface ICommand {
    String[] getAliases();
    void onCommand(Player player, String[] args);
}
