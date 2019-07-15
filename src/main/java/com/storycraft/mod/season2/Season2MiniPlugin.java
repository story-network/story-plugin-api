package com.storycraft.mod.season2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.MiniPlugin;
import com.storycraft.core.advancement.AdvancementType;
import com.storycraft.core.hologram.Hologram;
import com.storycraft.core.hologram.ShortHologram;
import com.storycraft.core.spawn.PlayerSpawnManager;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Season2MiniPlugin extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private Map<String, Hologram> spawnHologramList;

    @Override
    public void onLoad(StoryPlugin plugin) {
        spawnHologramList = new HashMap<>();
        plugin.getConfigManager().addConfigFile("session2.json", configFile = new JsonConfigPrettyFile()).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        getPlugin().getCommandManager().addCommand(new CommandReset());

        for (Player p : getPlugin().getServer().getOnlinePlayers())
            addSpawnHologram(p);
    }

    @Override
    public void onDisable(boolean reload) {
        for (Player p : getPlugin().getServer().getOnlinePlayers())
            removeSpawnHologram(p);
    }

    public JsonConfigEntry getPlayerProfile(UUID uuid) {
        JsonConfigEntry entry = configFile.getObject(uuid.toString());

        if (entry == null) {
            configFile.set(uuid.toString(), entry = configFile.createEntry());
        }

        return entry;
    }

    public long getFirstJoin(UUID uuid) {
        try {
            return getPlayerProfile(uuid).get("firstJoin").getAsLong();
        } catch (Exception e) {
            getPlayerProfile(uuid).set("firstJoin", -1);

            return -1;
        }
    }

    public boolean hasJoined(UUID uuid) {
        return getFirstJoin(uuid) != -1;
    }

    protected void addSpawnHologram(Player p) {
        removeSpawnHologram(p);

        Location spawnLoc = getPlugin().getPlayerManager().getPlayerSpawnManager().getSpawnLocation(p.getUniqueId());

        Hologram spawnHologram = new ShortHologram(spawnLoc.add(0, 1.5, 0),
                ChatColor.GREEN + p.getName() + " 의 스폰 위치", ChatColor.GOLD + "(0, 0) 까지 거리: " + ChatColor.WHITE + ""
                        + Math.sqrt(Math.pow(spawnLoc.getBlockX(), 2) + Math.pow(spawnLoc.getBlockZ(), 2)) + " 블록");

        getPlugin().getDecorator().getHologramManager().addHologram(spawnHologram);

        spawnHologramList.put(p.getName(), spawnHologram);
    }

    protected void removeSpawnHologram(Player p) {
        if (spawnHologramList.containsKey(p.getName()))
            getPlugin().getDecorator().getHologramManager().removeHologram(spawnHologramList.remove(p.getName()));
    }

    protected void firstJoinHandler(Player p) {
        Location spawnLoc = setRandomSpawn(p.getUniqueId());

        p.teleportAsync(spawnLoc).thenApply((Boolean b) -> {
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Reset", "스폰 위치는 최초 접속후 30분 내 무제한 리셋 할 수 있습니다. 커맨드: " + ChatColor.WHITE + "/reset"));
            return null;
        });

        getPlayerProfile(p.getUniqueId()).set("firstJoin", System.currentTimeMillis());
        getPlugin().getDecorator().getAdvancementManager().sendToastToPlayer(p, "StoryServer 플레이어 프로필 생성 완료", AdvancementType.TASK, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        p.sendTitle(ChatColor.YELLOW + " 스폰 지역 생성중...", ChatColor.WHITE + "잠시만 기다려주세요", 10, 30, 10);
    }

    protected Location setRandomSpawn(UUID id, int minRange, int maxRange) {
        PlayerSpawnManager spawnManager = getPlugin().getPlayerManager().getPlayerSpawnManager();

        World w = getPlugin().getServer().getWorld("world");
        Location randomSpawn = new Location(w,
                Math.floor(minRange + Math.random() * maxRange) * (Math.round(Math.random()) > 0 ? 1 : -1), 0,
                Math.floor(minRange + Math.random() * maxRange) * (Math.round(Math.random()) > 0 ? 1 : -1));

        randomSpawn = w.getHighestBlockAt(randomSpawn).getLocation().add(0, 2, 0);

        spawnManager.setSpawnEnabled(id, true);
        spawnManager.setSpawnLocation(id, randomSpawn);

        return randomSpawn;
    }

    protected Location setRandomSpawn(UUID id) {
        return setRandomSpawn(id, 5000, 25000);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        if (!hasJoined(e.getPlayer().getUniqueId())) {
            firstJoinHandler(e.getPlayer());
        }

        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
            addSpawnHologram(e.getPlayer());
        }, 30);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        removeSpawnHologram(e.getPlayer());
    }

    public class CommandReset implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "reset" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Player p = (Player) sender;

            if (System.currentTimeMillis() - getFirstJoin(p.getUniqueId()) > 1000 * 60 * 30 && !p.hasPermission("server.season2.reset.bypass")) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Reset", "최초 접속후 30분이 지나 더 이상 초기화를 할 수 없습니다"));
                return;
            }

            if (args.length < 1) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Reset", "스폰 위치가 초기화되고 인벤 내 모든 아이템이 소멸됩니다. 계속하려면 해당 커맨드를 입력하세요. " + ChatColor.WHITE + "/reset confirm"));
            } else if ("confirm".equals(args[0])) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Reset", "스폰 위치 초기화중..."));

                p.teleportAsync(setRandomSpawn(p.getUniqueId())).thenApply((Boolean b) -> {
                    p.getInventory().clear();

                    removeSpawnHologram(p);
                    addSpawnHologram(p);

                    p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "Reset", "초기화가 완료 되었습니다"));
                    return null;
                });
            }
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
            return false;
        }

    }
}
