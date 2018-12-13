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

    private String[] defaultAllowedPermList;
    private String[] defaultBlockedPermList;

    ServerRank(ChatColor nameColor, int rankLevel, String[] defaultAllowedPermList, String[] defaultBlockedPermList) {
        this.nameColor = nameColor;
        this.rankLevel = rankLevel;

        this.defaultAllowedPermList = defaultAllowedPermList;
        this.defaultBlockedPermList = defaultBlockedPermList;
    }

    ServerRank(ChatColor nameColor, int rankLevel, String[] defaultAllowedPermList) {
        this(nameColor, rankLevel, defaultAllowedPermList, new String[0]);
    }

    ServerRank(ChatColor nameColor, int rankLevel) {
        this(nameColor, rankLevel, new String[0], new String[0]);
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public int getRankLevel() {
        return rankLevel;
    }

    public String[] getDefaultAllowedPermList() {
        return defaultAllowedPermList;
    }

    public String[] getDefaultBlockedPermList() {
        return defaultBlockedPermList;
    }
}
