package com.storycraft.command;

import com.mojang.brigadier.ParseResults;
import com.storycraft.core.rank.RankManager;

import org.bukkit.command.CommandSender;

public interface ICommand {

    String[] getAliases();
    void onCommand(CommandSender sender, String[] args);

    boolean availableOnConsole();
    boolean availableOnCommandBlock();
    
    default int getRequiredRankLevel() {
        return RankManager.DEFAULT_RANK.getRankLevel();
    }
}
