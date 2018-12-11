package com.storycraft.core.rank;

import org.bukkit.ChatColor;

public enum ServerRank {

    ADMIN(ChatColor.AQUA, 4, new String[] {"*"}),
    DEVELOPER(ChatColor.LIGHT_PURPLE, 4, new String[] {"*"}),
    MOD(ChatColor.RED, 3),
    USER(ChatColor.YELLOW, 0),
    BLOCKED(ChatColor.GRAY, -1);

    private int rankLevel;
    private ChatColor nameColor;

    private String[] defaultPermissionList;

    ServerRank(ChatColor nameColor, int rankLevel) {
        this.nameColor = nameColor;
        this.rankLevel = rankLevel;

        this.defaultPermissionList = new String[0];
    }

    ServerRank(ChatColor nameColor, int rankLevel, String[] defaultPermissionList) {
        this.nameColor = nameColor;
        this.rankLevel = rankLevel;

        this.defaultPermissionList = defaultPermissionList;
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public int getRankLevel() {
        return rankLevel;
    }

    public String[] getDefaultPermission() {
        return defaultPermissionList;
    }
}
