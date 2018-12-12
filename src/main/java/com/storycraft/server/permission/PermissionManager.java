package com.storycraft.server.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.rank.RankManager;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.ServerExtension;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissibleBase;

public class PermissionManager extends ServerExtension implements Listener {

    private WrappedField<PermissibleBase, CraftHumanEntity> permField;
    
    private JsonConfigFile configFile;

    private Map<Player, PermissibleBase> playerTrackMap;

    public PermissionManager() {
        permField = Reflect.getField(CraftHumanEntity.class, "perm");
        playerTrackMap = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("permission.json", configFile = new JsonConfigFile()).getSync();
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
        for (Player p : playerTrackMap.keySet()) {
            unInjectPlayer(p);
        }
    }

    public List<String> getAllowedList(ServerRank rank) {
        List<String> list;

        JsonConfigEntry entry = configFile.getObject(rank.name());
        if (entry == null)
            configFile.set(rank.name(), entry = configFile.createEntry());

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

    public List<String> getBlockedList(ServerRank rank) {
        List<String> list;

        JsonConfigEntry entry = configFile.getObject(rank.name());
        if (entry == null)
            configFile.set(rank.name(), entry = configFile.createEntry());

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

    public RankManager getRankManager(){
        return getPlugin().getRankManager();
    }

    public boolean isInjected(Player p) {
        return playerTrackMap.containsKey(p);
    }

    public void injectToPlayer(Player p) {
        if (isInjected(p)) {
            unInjectPlayer(p);
        }

        permField.set((CraftHumanEntity) p, createManaged(p));

        playerTrackMap.put(p, permField.get((CraftHumanEntity) p));
    }

    public void unInjectPlayer(Player p) {
        if (isInjected(p)) {
            permField.set((CraftHumanEntity) p, playerTrackMap.get(p));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        injectToPlayer(e.getPlayer());
    }

    public PermissibleManaged createManaged(Player p) {
        PermissibleManaged managed = new PermissibleManaged(this, permField.get((CraftHumanEntity) p));

        ServerRank rank = getRankManager().getRank(p);

        managed.setAllowPermList(getAllowedList(rank));
        managed.setBlocked(getBlockedList(rank));

        return managed;
    }

}