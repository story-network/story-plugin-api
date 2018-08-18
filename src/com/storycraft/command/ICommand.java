package com.storycraft.command;

import com.storycraft.core.rank.RankManager;
import org.bukkit.entity.Player;

public interface ICommand {
    String[] getAliases();
    void onCommand(Player player, String[] args);

    default int getRequiredRankLevel() {
        return RankManager.DEFAULT_RANK.getRankLevel();
    }
}
