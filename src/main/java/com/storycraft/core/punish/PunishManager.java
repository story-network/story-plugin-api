package com.storycraft.core.punish;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.punish.punishment.*;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PunishManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private Map<String, IPunishment> punishmentList;

    private Map<UUID, Map<PunishmentInfo, IPunishment.PunishmentHandler>> handlerMap;

    public PunishManager() {
        this.punishmentList = new HashMap<>();
        this.handlerMap = new HashMap<>();
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

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            addPlayerHandler(p.getUniqueId());
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        addPlayerHandler(e.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removePlayerHandler(e.getPlayer().getUniqueId());
    }

    protected void addPlayerHandler(UUID id) {
        List<PunishmentInfo> infoList = getPlayerPunishment(id);

        Map<PunishmentInfo, IPunishment.PunishmentHandler> playerHandlerMap = getPlayerHandlerMap(id);

        for (PunishmentInfo info : infoList) {
            IPunishment.PunishmentHandler handler = info.getType().createPunishmentHandler(id);
            getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
            playerHandlerMap.put(info, handler);
        }
    }

    protected void removePlayerHandler(UUID id) {
        List<PunishmentInfo> infoList = getPlayerPunishment(id);

        Map<PunishmentInfo, IPunishment.PunishmentHandler> playerHandlerMap = getPlayerHandlerMap(id);

        for (PunishmentInfo info : playerHandlerMap.keySet()) {
            IPunishment.PunishmentHandler handler = playerHandlerMap.get(info);

            HandlerList.unregisterAll(handler);
        }

        handlerMap.remove(id);
    }

    protected Map<PunishmentInfo, IPunishment.PunishmentHandler> getPlayerHandlerMap(UUID id) {
        if (handlerMap.containsKey(id))
            return handlerMap.get(id);
        else {
            Map<PunishmentInfo, IPunishment.PunishmentHandler> map = new HashMap<>();

            handlerMap.put(id, map);

            return map;
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

    public void removeAllPlayerPunishment(UUID id) {
        setPlayerPunishment(id, new ArrayList<>());
    }

    public List<PunishmentInfo> getPlayerPunishment(UUID id) {
        JsonConfigEntry entry = getIdEntry(id);
        List<PunishmentInfo> list = null;
        try {
            JsonArray array = entry.get("punishment_list").getAsJsonArray();

            PunishmentInfo[] arr = new PunishmentInfo[array.size()];

            for (int i = 0; i < arr.length; i++) {
                JsonConfigEntry obj = new JsonConfigEntry(array.get(i).getAsJsonObject());
                IPunishment punishment = getPunishment(obj.get("type").getAsString());

                if (punishment == null)
                    continue;

                arr[i] = new PunishmentInfo(punishment, obj.get("expire_at").getAsLong());
            }
            
            list = Lists.newArrayList(arr);

        } catch (Exception e) {
            setPlayerPunishment(id, list = new ArrayList<>());
        }

        return list;
    }

    public void setPlayerPunishment(UUID id, List<PunishmentInfo> list) {
        JsonConfigEntry entry = getIdEntry(id);

        entry.set("punishment_list", list);
    }

    public void addPlayerPunishment(UUID id, String name) {
        IPunishment punishment = getPunishment(name);

        if (punishment == null)
            return;

        addPlayerPunishment(id, new PunishmentInfo(punishment, -1));
    }

    public void addPlayerPunishment(UUID id, String name, long expireAt) {
        IPunishment punishment = getPunishment(name);

        if (punishment == null)
            return;

        addPlayerPunishment(id, new PunishmentInfo(punishment, expireAt));
    }

    public void addPlayerPunishment(UUID id, PunishmentInfo info) {
        List<PunishmentInfo> punishList = getPlayerPunishment(id);

        punishList.add(info);

        setPlayerPunishment(id, punishList);
    }

    public void removePlayerPunishment(UUID id, String name) {
        IPunishment punishment = getPunishment(name);

        if (punishment == null)
            return;

        List<PunishmentInfo> punishList = getPlayerPunishment(id);

        for (PunishmentInfo info : new ArrayList<>(punishList)) {
            if (info.getType().equals(punishment)) {
                punishList.remove(info);
            }
        }

        setPlayerPunishment(id, punishList);
    }

    public void removePlayerPunishment(UUID id, PunishmentInfo info) {
        List<PunishmentInfo> punishList = getPlayerPunishment(id);

        if (punishList.remove(info))
            setPlayerPunishment(id, punishList);
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

    public boolean containsPunishment(IPunishment punishment) {
        return punishmentList.containsValue(punishment);
    }

    public String getName(IPunishment punishment) {
        if (!containsPunishment(punishment))
            return null;

        for (String name : punishmentList.keySet()) {
            IPunishment p = punishmentList.get(name);

            if (p.equals(punishment))
                return name;
        }
        
        return null;
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

                List<PunishmentInfo> list = getPlayerPunishment(offline.getUniqueId());

                if (list == null || list.isEmpty()) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 은(는) 아무런 제한도 갖고있지 않습니다"));
                }
                else {
                    String[] punishList = new String[list.size()];

                    for (int i = 0; i < punishList.length; i++) {
                        PunishmentInfo info = list.get(i);
                        punishList[i] = (info.getExpireAt() > 0 ? ChatColor.GOLD + new Date(info.getExpireAt()).toGMTString() + ChatColor.WHITE + " 까지 " : "") + ChatColor.YELLOW + getName(info.getType()) + ChatColor.WHITE + " 제한";
                    }

                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 가 가지고 있는 제한 목록: " + String.join("\n", punishList)));
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

                int size = getPlayerPunishment(offline.getUniqueId()).size();

                if (size < 1) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "플레이어 " + name + " 은(는) 아무 제한을 갖고 있지 않습니다"));
                    return;
                }

                removeAllPlayerPunishment(offline.getUniqueId());

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 의 제한 " + size + " 개 를 모두 제거했습니다"));
            }
            else if ("add".equals(option)) {
                if (args.length < 3) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish add <플레이어 이름> <punishment> [지속시간]"));
                    return;
                }

                String name = args[1];
                String punishName = args[2];

                long expireAt = -1;

                if (args.length > 3) {
                    try {
                        expireAt = System.currentTimeMillis() + Long.parseLong(args[3]);
                    } catch (Exception e) {}
                }

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

                addPlayerPunishment(offline.getUniqueId(), punishName, expireAt);

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 에게 " + punishName + " 제한을 " + (expireAt > 0 ? new Date(expireAt).toGMTString() + " 까지 " : "") + " 적용합니다"));

                if (offline.isOnline()) {
                    offline.getPlayer().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PunishManager", sender.getName() + "에 의해 " + punishName + " 제한이 적용되었습니다"));
                }
            }
            else {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish <add / get / clear / list>"));
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