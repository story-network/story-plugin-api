package com.storycraft.core.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.config.event.ConfigUpdateEvent;
import com.storycraft.core.rank.RankManager;
import com.storycraft.core.rank.RankUpdateEvent;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.ServerExtension;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.permissions.PermissibleBase;

public class PermissionManager extends ServerExtension implements Listener {

    private WrappedField<PermissibleBase, CraftHumanEntity> permField;
    
    private JsonConfigFile rankConfigFile;
    private JsonConfigFile playerConfigFile;

    private Map<UUID, PermissibleBase> playerTrackMap;

    public PermissionManager() {
        permField = Reflect.getField(CraftHumanEntity.class, "perm");
        playerTrackMap = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("permission_rank.json", rankConfigFile = new JsonConfigPrettyFile()).getSync();
            plugin.getConfigManager().addConfigFile("permission_player.json", playerConfigFile = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            injectToPlayer(p);
        }
    }

    @Override
    public void onDisable(boolean reload) {
        for (UUID id : playerTrackMap.keySet()) {
            Player p = getPlugin().getServer().getPlayer(id);

            if (p == null)
                continue;
            
            unInjectPlayer(p);
        }
    }

    protected JsonConfigEntry getPlayerPermissionEntry(Player p) {
        JsonConfigEntry entry = playerConfigFile.getObject(p.getUniqueId().toString());
        if (entry == null)
            playerConfigFile.set(p.getUniqueId().toString(), entry = playerConfigFile.createEntry());

        return entry;
    }

    public List<String> getPlayerAllowedList(Player p) {
        List<String> list;

        JsonConfigEntry entry = getPlayerPermissionEntry(p);

        try {
            JsonArray array = entry.get("allowed").getAsJsonArray();

            list = new ArrayList<>(array.size());

            for (JsonElement element : array) {
                list.add(element.getAsString());
            }
        } catch (Exception e) {
            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PermissionManager", "플레이어 " + p.getName() + " 의 버킷 api 펄미션 허용 목록이 비었거나 오류가 있습니다. 기본 설정으로 되돌립니다"));
            entry.set("allowed", list = new ArrayList<>());
        }

        return list;
    }

    public List<String> getPlayerBlockedList(Player p) {
        List<String> list;

        JsonConfigEntry entry = getPlayerPermissionEntry(p);

        try {
            JsonArray array = entry.get("blocked").getAsJsonArray();

            list = new ArrayList<>(array.size());

            for (JsonElement element : array) {
                list.add(element.getAsString());
            }
        } catch (Exception e) {
            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PermissionManager", "플레이어 " + p.getName() + " 의 버킷 api 펄미션 차단 목록이 비었거나 오류가 있습니다. 기본 설정으로 되돌립니다"));
            entry.set("blocked", list = new ArrayList<>());
        }

        return list;
    }

    protected JsonConfigEntry getRankPermissionEntry(ServerRank rank) {
        JsonConfigEntry entry = rankConfigFile.getObject(rank.name());
        if (entry == null)
            rankConfigFile.set(rank.name(), entry = rankConfigFile.createEntry());

        return entry;
    }

    public List<String> getRankAllowedList(ServerRank rank) {
        List<String> list;

        JsonConfigEntry entry = getRankPermissionEntry(rank);

        try {
            JsonArray array = entry.get("allowed").getAsJsonArray();

            list = new ArrayList<>(array.size());

            for (JsonElement element : array) {
                list.add(element.getAsString());
            }
        } catch (Exception e) {
            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PermissionManager", "랭크 " + rank.name() + " 의 버킷 api 펄미션 허용 목록이 비었거나 오류가 있습니다. 기본 설정으로 되돌립니다"));
            entry.set("allowed", list = Lists.newArrayList(rank.getDefaultAllowedPermList()));
        }

        return list;
    }

    public List<String> getRankBlockedList(ServerRank rank) {
        List<String> list;

        JsonConfigEntry entry = getRankPermissionEntry(rank);

        try {
            JsonArray array = entry.get("blocked").getAsJsonArray();

            list = new ArrayList<>(array.size());

            for (JsonElement element : array) {
                list.add(element.getAsString());
            }
        } catch (Exception e) {
            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PermissionManager", "랭크 " + rank.name() + " 의 버킷 api 펄미션 차단 목록이 비었거나 오류가 있습니다. 기본 설정으로 되돌립니다"));
            entry.set("blocked", list = Lists.newArrayList(rank.getDefaultBlockedPermList()));
        }

        return list;
    }

    public List<String> getAllowedList(ServerRank rank, Player p) {
        List<String> list = getRankAllowedList(rank);
        List<String> playerAllowedList = getPlayerAllowedList(p);

        for (String s : playerAllowedList) {
            if (!list.contains(s))
                list.add(s);
        }

        return list;
    }

    public List<String> getBlockedList(ServerRank rank, Player p) {
        List<String> list = getRankBlockedList(rank);
        List<String> playerAllowedList = getPlayerBlockedList(p);

        for (String s : playerAllowedList) {
            if (!list.contains(s))
                list.add(s);
        }

        return list;
    }

    public RankManager getRankManager(){
        return getPlugin().getCoreManager().getRankManager();
    }

    public boolean isInjected(Player p) {
        return playerTrackMap.containsKey(p.getUniqueId());
    }

    public void injectToPlayer(Player p) {
        if (isInjected(p)) {
            unInjectPlayer(p);
        }

        permField.set((CraftHumanEntity) p, createManaged(p));

        playerTrackMap.put(p.getUniqueId(), permField.get((CraftHumanEntity) p));
    }

    public void unInjectPlayer(Player p) {
        if (isInjected(p)) {
            PermissibleBase base = playerTrackMap.get(p.getUniqueId());
            permField.set((CraftHumanEntity) p, base);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent e) {
        injectToPlayer(e.getPlayer());
    }

    @EventHandler
    public void onRankUpdate(RankUpdateEvent e) {
        if (isInjected(e.getPlayer())) {
            PermissibleManaged managed = (PermissibleManaged) permField.get((CraftHumanEntity) e.getPlayer());

            managed.setAllowPermList(getAllowedList(e.getTo(), e.getPlayer()));
            managed.setBlockPermList(getBlockedList(e.getTo(), e.getPlayer()));
            managed.recalculatePermissions();
        }
        else
            injectToPlayer(e.getPlayer());

        e.getPlayer().updateCommands();
    }

    @EventHandler
    public void onConfigReload(ConfigUpdateEvent e) {
        if (e.getConfig().equals(rankConfigFile) || e.getConfig().equals(playerConfigFile)) {
            for (UUID id : playerTrackMap.keySet()) {
                Player p = getPlugin().getServer().getPlayer(id);

                if (p == null)
                    continue;

                PermissibleManaged managed = (PermissibleManaged) permField.get((CraftHumanEntity) p);

                ServerRank rank = getRankManager().getRank(p);

                managed.setAllowPermList(getAllowedList(rank, p));
                managed.setBlockPermList(getBlockedList(rank, p));
                managed.recalculatePermissions();
            }
        }
    }

    public PermissibleManaged createManaged(Player p) {
        PermissibleManaged managed = new PermissibleManaged(this, permField.get((CraftHumanEntity) p));

        ServerRank rank = getRankManager().getRank(p);

        managed.setAllowPermList(getAllowedList(rank, p));
        managed.setBlockPermList(getBlockedList(rank, p));
        managed.recalculatePermissions();

        return managed;
    }

}