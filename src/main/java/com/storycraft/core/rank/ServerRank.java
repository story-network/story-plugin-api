package com.storycraft.core.rank;

import org.bukkit.ChatColor;

public enum ServerRank {
    DEVELOPER(ChatColor.LIGHT_PURPLE, 4),
    MOD(ChatColor.RED, 3),
    USER(ChatColor.YELLOW, 0),
    BLOCKED(ChatColor.RESET, -1);

    private int rankLevel;
    private ChatColor nameColor;

    ServerRank(ChatColor nameColor, int rankLevel) {
        this.nameColor = nameColor;
        this.rankLevel = rankLevel;
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public int getRankLevel() {
        return rankLevel;
    }
}
