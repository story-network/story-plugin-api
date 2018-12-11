package com.storycraft.core.rank;

import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.config.ConfigUpdateEvent;
import com.storycraft.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.md_5.bungee.api.ChatColor;

import java.util.StringJoiner;

public class RankManager extends MiniPlugin implements ICommand, Listener {

    public static final ServerRank DEFAULT_RANK = ServerRank.USER;

    private JsonConfigFile configFile;
    private JsonConfigFile permConfigFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("rank.json", configFile = new JsonConfigFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            plugin.getConfigManager().addConfigFile("perm.json", permConfigFile = new JsonConfigFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        for (Player p : getPlugin().getServer().getOnlinePlayers())
            updatePlayerPerm(p);
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
        return new String[] { "rank" };
    }

    @Override
    public int getRequiredRankLevel() {
        return ServerRank.DEVELOPER.getRankLevel();
    }

    public String[] getBukkitPerms(ServerRank rank) {
        if (permConfigFile.contains(rank.name()) && permConfigFile.get(rank.name()).isJsonArray()) {
            JsonArray array = permConfigFile.get(rank.name()).getAsJsonArray();

            String[] list = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                list[i] = array.get(i).getAsString();
            }

            return list;
        }
        else {
            JsonArray array = new JsonArray();
            for (String perm : rank.getDefaultPermission()) {
                array.add(perm);
            }
            permConfigFile.set(rank.name(), array);

            return rank.getDefaultPermission();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        updatePlayerPerm(e.getPlayer());
    }

    @EventHandler
    public void onConfigUpdate(ConfigUpdateEvent e) {
        if (permConfigFile.equals(e.getConfig())) {
            for (Player p : getPlugin().getServer().getOnlinePlayers())
                updatePlayerPerm(p);
        }
    }

    protected void updatePlayerPerm(Player p) {
        p.getEffectivePermissions();
    }

    public boolean hasPermission(Player p, ServerRank minRank) {
        return getRank(p).getRankLevel() >= minRank.getRankLevel();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank <set/get/list>"));
            return;
        }

        if ("set".equals(args[0])) {
            if (args.length < 3) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank set <플레이어 이름> <랭크>"));
                return;
            }

            String name = args[1];
            String rankName = args[2];
    
            Player p = getPlugin().getServer().getPlayer(name);
    
            if (p == null) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "플레이어 " + name + " 을 찾을 수 없습니다."));
                return;
            }
    
            boolean found = false;
            for (ServerRank r : ServerRank.values()) {
                if (r.name().equals(rankName)) {
                    found = true;
                    break;
                }
            }
    
            if (!found) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "알 수 없는 랭크 입니다"));
                return;
            }
    
            ServerRank rank = ServerRank.valueOf(rankName);
    
            getPlugin().getRankManager().setRank(p, rank);
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "RankManager", p.getName() + " 의 랭크는 이제 " + rank.name() + " 입니다"));
        }
        else if ("get".equals(args[0])) {
            if (args.length < 2) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank get <플레이어 이름>"));
                return;
            }

            String name = args[1];

            Player p = getPlugin().getServer().getPlayer(name);
    
            if (p == null) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "플레이어 " + name + " 을 찾을 수 없습니다."));
                return;
            }

            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.ALERT, "RankManager", p.getName() + " 의 랭크는 " + ChatColor.WHITE + getPlugin().getRankManager().getRank(p).toString() + ChatColor.GRAY + " 입니다"));
        }
        else if ("list".equals(args[0])) {
            StringJoiner sj = new StringJoiner(", ");
                for (ServerRank r : ServerRank.values())
                    sj.add(r.name());
    
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용 가능한 랭크 목록: " + sj.toString()));
        }
        else {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "RankManager", "사용법 /rank <set/get/list>"));
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
