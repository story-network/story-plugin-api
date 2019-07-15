package com.storycraft.core.player.home;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class HomeManager extends MiniPlugin implements Listener {

    private JsonConfigFile homeConfigFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("home.json", homeConfigFile = new JsonConfigFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        plugin.getCommandManager().addCommand(new HomeCommand());
        plugin.getCommandManager().addCommand(new SetHomeCommand());
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void setPlayerHome(Player player, Location location) {
        setRespawnLocation(player, location);
        player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "HomeManager", "집 위치가 " + location.getWorld().getName() + " " + (Math.floor(location.getX() * 10) / 10) + " " + (Math.floor(location.getY() * 10) / 10) + " " + (Math.floor(location.getZ() * 10) / 10) + " 로 지정되었습니다."));
    }

    public int getTeleportCoolTime() {
        JsonConfigEntry entry = getOptionEntry();

        try {
            return entry.get("cooltime").getAsInt();
        } catch (Exception e) {
            setTeleportCoolTime(60000);

            return 60000;
        }
    }

    public void setTeleportCoolTime(int coolTime) {
        JsonConfigEntry entry = getOptionEntry();
        entry.set("cooltime", coolTime);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Location respawnLocation = getRespawnLocation(e.getPlayer());

        if (respawnLocation != null)
            e.setRespawnLocation(respawnLocation);
    }

    public Location getRespawnLocation(OfflinePlayer p) {
        JsonConfigEntry entry;

        if (!homeConfigFile.contains(p.getUniqueId().toString()) || (entry = homeConfigFile.getObject(p.getUniqueId().toString())) == null)
            return null;

        try {
            return new Location(getPlugin().getServer().getWorld(entry.get("world").getAsString()), entry.get("x").getAsDouble(), entry.get("y").getAsDouble(), entry.get("z").getAsDouble(), entry.get("yaw").getAsFloat(), entry.get("pitch").getAsFloat());
        } catch (Exception e) {
            return null;
        }
    }

    public void setRespawnLocation(Player p, Location location) {
        p.setBedSpawnLocation(location);

        JsonConfigEntry entry = new JsonConfigEntry();

        entry.set("world", location.getWorld().getName());
        entry.set("x", location.getX());
        entry.set("y", location.getY());
        entry.set("z", location.getZ());
        entry.set("yaw", location.getYaw());
        entry.set("pitch", location.getPitch());

        homeConfigFile.set(p.getUniqueId().toString(), entry);
    }

    public JsonConfigEntry getOptionEntry() {
        JsonConfigEntry entry = homeConfigFile.getObject("options");

        if (entry == null) {
            homeConfigFile.set("options", entry = homeConfigFile.createEntry());
        }

        return entry;
    }

    public class HomeCommand implements ICommand {

        private Map<UUID, Long> timeMap; 

        public HomeCommand() {
            this.timeMap = new HashMap<>();
        }

        @Override
        public String[] getAliases() {
            return new String[]{ "home" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Player p = (Player) sender;

            if (getPlugin().getRankManager().hasPermission(p, ServerRank.MOD) && args.length > 0){
                String targetPlayer = args[0];
                OfflinePlayer pl = getPlugin().getServer().getOfflinePlayer(targetPlayer);
                if (pl == null) {
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "HomeManager", "플레이어 " + targetPlayer + " 을(를) 찾을 수 없습니다"));
                }
                else {
                    Location location = getRespawnLocation(pl);

                    if (location == null) {
                        p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "HomeManager", "플레이어 " + targetPlayer + " 의 집이 설정되어있지 않습니다"));
                        return;
                    }

                    p.teleport(location);
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "HomeManager", targetPlayer + " 의 집으로 이동되었습니다"));
                }
            }
            else {
                Location location = getRespawnLocation(p);

                if (location == null) {
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "HomeManager", "집 위치가 지정되어 있지 않습니다. /sethome을 사용해 지정후 사용해 주세요"));
                    return;
                }

                if (isCoolTimeDone(p) || getPlugin().getRankManager().hasPermission(p, ServerRank.MOD)) {
                    p.teleport(location);
                    timeMap.put(p.getUniqueId(), System.currentTimeMillis());
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "HomeManager", "집으로 이동되었습니다"));
                }
                else {
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "HomeManager", "다음 커맨드 사용까지 " + Math.ceil((getTeleportCoolTime() - (System.currentTimeMillis() - getLastTeleport(p))) / 1000) + " 초 더 기다려야 합니다"));
                }
            }
        }

        protected void updateLastTeleport(Player p) {
            if (timeMap.containsKey(p.getUniqueId()))
                timeMap.replace(p.getUniqueId(), System.currentTimeMillis());
            else
                timeMap.put(p.getUniqueId(), System.currentTimeMillis());
        }

        protected long getLastTeleport(Player p) {
            if (!timeMap.containsKey(p.getUniqueId()))
                return 0;
            else
                return timeMap.get(p.getUniqueId());
        }

        protected boolean isCoolTimeDone(Player p) {
            return System.currentTimeMillis() - getLastTeleport(p) >= getTeleportCoolTime();
        }

        @Override
        public boolean availableOnConsole() {
            return false;
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
            return "server.command.home";
        }

    }

    public class SetHomeCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[]{ "sethome" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Player p = (Player) sender;
            Location location = p.getLocation();

            if (args.length > 0) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "HomeManager", "집은 한개만 설정가능합니다"));
                return;
            }

            setPlayerHome(p, location);
        }

        @Override
        public boolean availableOnConsole() {
            return false;
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
            return "server.command.sethome";
        }

    }

}
