package com.storycraft.core.punish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.config.ConfigUpdateEvent;
import com.storycraft.core.punish.IPunishment.PunishmentHandler;
import com.storycraft.core.punish.punishment.*;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.json.simple.JSONArray;

public class PunishManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private Map<String, IPunishment> punishmentList;

    private Map<UUID, List<IPunishment.PunishmentHandler>> handlerList;

    public PunishManager() {
        this.punishmentList = new HashMap<>();
        this.handlerList = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        addPunishment("freeze", new FreezePunishment());
        addPunishment("mute", new MutePunishment());

        try {
            plugin.getConfigManager().addConfigFile("punishment.json", configFile = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        plugin.getCommandManager().addCommand(new PunishCommand());
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        loadConfig();
    }

    private void loadConfig() {
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            List<String> nameList = getPlayerPunishment(p.getUniqueId());
                for (String name : nameList) {
                    if (name != null) {
                        IPunishment punishment = getPunishment(name);

                        if (punishment != null) {
                            addHandler(p.getUniqueId(), punishment);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onConfigReload(ConfigUpdateEvent e) {
        if (configFile.equals(e.getConfig())) {
            for (Player p : getPlugin().getServer().getOnlinePlayers()) {
                removeAllHandler(p.getUniqueId());
            }

            loadConfig();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == Result.ALLOWED && hasPunishment(e.getUniqueId())) {
            List<String> punishList = getPlayerPunishment(e.getUniqueId());

            for (String name : punishList) {
                IPunishment punishment = getPunishment(name);

                if (punishment == null)
                    return;

                addHandler(e.getUniqueId(), punishment);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (hasHandler(e.getPlayer().getUniqueId())) {
            removeAllHandler(e.getPlayer().getUniqueId());
        }
    }

    public boolean containsPunishment(String name) {
        return punishmentList.containsKey(name);
    }

    protected JsonConfigEntry getIdEntry(UUID id) {
        JsonConfigEntry entry = configFile.getObject(id.toString());

        if (entry == null)
            configFile.set(id.toString(), entry = configFile.createEntry());

        return entry;
    }

    public boolean hasPunishment(UUID id) {
        JsonConfigEntry entry = getIdEntry(id);

        try {
            return entry.get("punishment_enabled").getAsBoolean();
        } catch (Exception e) {
            entry.set("punishment_enabled", false);

            return false;
        }
    }

    public void setPunishmentEnabled(UUID id, boolean flag) {
        JsonConfigEntry entry = getIdEntry(id);

        entry.set("punishment_enabled", flag);
    }

    public void removeAllPlayerPunishment(UUID id) {
        setPunishmentEnabled(id, false);

        removeAllHandler(id);
    }

    public List<String> getPlayerPunishment(UUID id) {
        if (!hasPunishment(id))
            return null;
        
        JsonConfigEntry entry = getIdEntry(id);
        List<String> list = null;
        try {
            JsonArray array = entry.get("punishment_list").getAsJsonArray();
            list = new ArrayList<>(array.size());

            for (int i = 0; i < list.size(); i++)
                list.set(i, array.get(i).getAsString());
        } catch (Exception e) {
            configFile.set("punishment_list", list = new ArrayList<>());
        }

        return list;
    }

    public void addPlayerPunishment(UUID id, String name) {
        JsonConfigEntry entry = getIdEntry(id);

        IPunishment punishment = getPunishment(name);

        if (punishment == null)
            return;

        setPunishmentEnabled(id, true);

        List<String> punishList = getPlayerPunishment(id);

        entry.set("punishment_list", punishList.add(name));

        addHandler(id, punishment);
    }

    protected void addHandler(UUID id, IPunishment punishment) {
        PunishmentHandler handler = punishment.createPunishmentHandler(id);

        List<PunishmentHandler> punishmentList;
        if (!hasHandler(id)) {
            handlerList.put(id, punishmentList = new ArrayList<>());
        }
        else {
            punishmentList = handlerList.get(id);
        }
        
        punishmentList.add(handler);
        getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    protected boolean hasHandler(UUID id) {
        return handlerList.containsKey(id);
    }

    protected void removeAllHandler(UUID id) {
        if (handlerList.containsKey(id)) {
            for (IPunishment.PunishmentHandler handler : handlerList.get(id))
                removeHandler(id, handler);
        }
    }

    protected void removeHandler(UUID id, IPunishment.PunishmentHandler handler) {
        if (handlerList.containsKey(id) && handlerList.get(id).remove(handler)) {
            HandlerList.unregisterAll(handler);
        }
    }

    public void addPunishment(String name, IPunishment punishment) {
        if (containsPunishment(name))
            return;

        punishmentList.put(name, punishment);
    }

    public IPunishment getPunishment(String name) {
        if (!containsPunishment(name))
            return null;
        
        return punishmentList.get(name);
    }

    public class PunishCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "punish" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish <add / get / clear / list>"));
                return;
            }

            String option = args[0];

            if ("list".equals(option)) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "사용가능한 Punishment 목록: " + String.join(", ", punishmentList.keySet())));
            }
            else if ("get".equals(option)) {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish get <플레이어 이름>"));
                    return;
                }

                String name = args[1];
                OfflinePlayer offline = getPlugin().getServer().getOfflinePlayer(name);

                if (offline == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 을(를) 찾을 수 없습니다"));
                    return;
                }

                List<String> list = getPlayerPunishment(offline.getUniqueId());

                if (list.isEmpty()) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 은(는) 아무런 제한도 갖고있지 않습니다"));
                }
                else {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 가 가지고 있는 제한 목록: " + String.join(", ", list)));
                }
            }
            else if ("clear".equals(option)) {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish clear <플레이어 이름>"));
                    return;
                }

                String name = args[1];
                OfflinePlayer offline = getPlugin().getServer().getOfflinePlayer(name);

                if (offline == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 을(를) 찾을 수 없습니다"));
                    return;
                }

                if (!hasPunishment(offline.getUniqueId())) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 은(는) 아무 제한을 갖고 있지 않습니다"));
                }
                else {
                    removeAllPlayerPunishment(offline.getUniqueId());

                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 의 제한 " + getPlayerPunishment(offline.getUniqueId()).size() + " 개 를 모두 제거했습니다"));
                }
            }
            else if ("add".equals(option)) {
                if (args.length < 3) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish add <플레이어 이름> <punishment>"));
                    return;
                }

                String name = args[1];
                String punishName = args[2];

                OfflinePlayer offline = getPlugin().getServer().getOfflinePlayer(name);
                IPunishment punishment = getPunishment(punishName);

                if (offline == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 을(를) 찾을 수 없습니다"));
                    return;
                }

                if (punishment == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "제한 " + punishName + " 을(를) 찾을 수 없습니다"));
                    return;
                }

                addPlayerPunishment(offline.getUniqueId(), punishName);

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 에게 " + punishName + " 제한을 적용했습니다"));

                if (offline.isOnline()) {
                    offline.getPlayer().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PunishManager", sender.getName() + "에 의해 " + punishName + " 제한이 적용되었습니다"));
                }
            }
            else {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish <set / remove / list>"));
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

        @Override
        public boolean isPermissionRequired() {
            return true;
        }
        
        @Override
        public String getPermissionRequired() {
            return "server.command.punish";
        }

    }
}