package com.storycraft.mod.session2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.hologram.Hologram;
import com.storycraft.core.hologram.ShortHologram;
import com.storycraft.core.spawn.PlayerSpawnManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Session2MiniPlugin extends MiniPlugin implements Listener {

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
        Hologram spawnHologram;

        spawnHologramList.remove(p.getName());
        
        getPlugin().getDecorator().getHologramManager().addHologram(spawnHologram = new ShortHologram(getPlugin().getPlayerManager().getPlayerSpawnManager().getSpawnLocation(p.getUniqueId()),
         ChatColor.GREEN + p.getName() + ChatColor.WHITE + " 의 스폰 위치"));

        spawnHologramList.put(p.getName(), spawnHologram);
    }

    protected void removeSpawnHologram(Player p) {
        if (spawnHologramList.containsKey(p.getName()))
            getPlugin().getDecorator().getHologramManager().removeHologram(spawnHologramList.remove(p.getName()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerSpawnManager spawnManager = getPlugin().getPlayerManager().getPlayerSpawnManager();

        if (!hasJoined(e.getPlayer().getUniqueId())) {
            World w = getPlugin().getServerManager().getWorldManager().getDefaultOverworld().getBukkitWorld();
            Location randomSpawn = new Location(w
            , Math.floor(7500 + Math.random() * 15000) * (Math.round(Math.random()) - 1)
            , 0
            , Math.floor(7500 + Math.random() * 15000) * (Math.round(Math.random()) - 1)
            );

            randomSpawn = w.getHighestBlockAt(randomSpawn).getLocation().add(0, 2, 0);

            spawnManager.setSpawnEnabled(e.getPlayer().getUniqueId(), true);
            spawnManager.setSpawnLocation(e.getPlayer().getUniqueId(), randomSpawn);
        }

        addSpawnHologram(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        removeSpawnHologram(e.getPlayer());
    }
}