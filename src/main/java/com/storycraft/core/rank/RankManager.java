package com.storycraft.core.rank;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.StringJoiner;

public class RankManager extends MiniPlugin implements ICommand {

    public static final ServerRank DEFAULT_RANK = ServerRank.USER;

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("rank.json", configFile = new JsonConfigFile()).run();
        plugin.getCommandManager().addCommand(this);
    }

    public ServerRank getRank(Player p) {
        if (!configFile.contains(p.getUniqueId().toString())) {
            setRank(p, DEFAULT_RANK);
            return DEFAULT_RANK;
        }

        try {
            return ServerRank.valueOf(configFile.get(p.getUniqueId().toString()).getAsString());
        } catch (Exception e) {
            setRank(p, DEFAULT_RANK);
            return DEFAULT_RANK;
        }
    }

    public void setRank(Player p, ServerRank rank) {
        configFile.set(p.getUniqueId().toString(), rank.name());
    }

    @Override
    public String[] getAliases() {
        return new String[] {"rank"};
    }

    @Override
    public int getRequiredRankLevel() {
        return ServerRank.DEVELOPER.getRankLevel();
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PlayerRank", "사용법 /rank <플레이어 이름> <랭크 이름>"));
            return;
        }

        String name = args[0];
        String rankName = args[1];

        Player p = getPlugin().getServer().getPlayer(name);

        if (p == null) {
            player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PlayerRank", "플레이어 " + name + " 을 찾을 수 없습니다."));
            return;
        }

        boolean found = false;
        for (ServerRank r : ServerRank.values()) {
            if (r.name().equalsIgnoreCase(rankName)) {
                found = true;
                break;
            }
        }

        if (!found) {
            StringJoiner sj = new StringJoiner(", ");
            for (ServerRank r : ServerRank.values())
                sj.add(r.name());

            player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PlayerRank", "사용 가능한 랭크 목록: " + sj.toString()));
            return;
        }

        ServerRank rank = ServerRank.valueOf(rankName);

        getPlugin().getRankManager().setRank(p, rank);
        player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PlayerRank", p.getName() + " 의 랭크는 이제 " + rank.name() + " 입니다"));
    }
}
