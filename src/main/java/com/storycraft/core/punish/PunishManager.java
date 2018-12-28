package com.storycraft.core.punish;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.punish.IPunishment.PunishmentHandler;
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

public class PunishManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private Map<String, IPunishment> punishmentList;

    private Map<UUID, IPunishment.PunishmentHandler> handlerList;

    public PunishManager() {
        this.punishmentList = new HashMap<>();
        this.handlerList = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {


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
            String name = getPlayerPunishment(p.getUniqueId());
            if (name != null) {
                IPunishment punishment = getPunishment(name);

                if (punishment != null) {
                    addHandler(p.getUniqueId(), punishment);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == Result.ALLOWED && hasPunishment(e.getUniqueId())) {
            IPunishment punishment = getPunishment(getPlayerPunishment(e.getUniqueId()));

            if (punishment == null)
                return;

            addHandler(e.getUniqueId(), punishment);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (hasHandler(e.getPlayer().getUniqueId())) {
            removeHandler(e.getPlayer().getUniqueId());
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

    public void removePlayerPunishment(UUID id) {
        setPunishmentEnabled(id, false);

        removeHandler(id);
    }

    public String getPlayerPunishment(UUID id) {
        if (!hasPunishment(id))
            return null;
        
        JsonConfigEntry entry = getIdEntry(id);

        try {
            return entry.get("punishment_type").getAsString();
        } catch (Exception e) {
            configFile.set("punishment_type", "");

            return "";
        }
    }

    public void setPlayerPunishment(UUID id, String name) {
        JsonConfigEntry entry = getIdEntry(id);

        IPunishment punishment = getPunishment(name);

        if (punishment == null)
            return;

        setPunishmentEnabled(id, true);

        entry.set("punishment_type", name);

        addHandler(id, punishment);
    }

    protected void addHandler(UUID id, IPunishment punishment) {
        PunishmentHandler handler = punishment.createPunishmentHandler(id);

        if (hasHandler(id))
            removeHandler(id);
        
        handlerList.put(id, handler);
        getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    protected boolean hasHandler(UUID id) {
        return handlerList.containsKey(id);
    }

    protected void removeHandler(UUID id) {
        if (handlerList.containsKey(id)) {
            HandlerList.unregisterAll(handlerList.remove(id));
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
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish <set / remove / list>"));
                return;
            }

            String option = args[0];

            if ("list".equals(option)) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "사용가능한 Punishment 목록: " + String.join(", ", punishmentList.keySet())));
            }
            else if ("remove".equals(option)) {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish remove <플레이어 이름>"));
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
                    removePlayerPunishment(offline.getUniqueId());

                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 의 제한 " + getPlayerPunishment(offline.getUniqueId()) + " 을(를) 제거했습니다"));
                }
            }
            else if ("set".equals(option)) {
                if (args.length < 3) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "PunishManager", "사용법 /punish remove <플레이어 이름> <punishment>"));
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

                setPlayerPunishment(offline.getUniqueId(), punishName);

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "PunishManager", "플레이어 " + name + " 에게 " + punishName + " 제한을 적용했습니다"));

                if (offline.isOnline()) {
                    offline.getPlayer().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "PunishManager", punishName + " 제한이 적용되었습니다"));
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