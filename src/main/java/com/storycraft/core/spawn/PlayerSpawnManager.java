package com.storycraft.core.spawn;

import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerSpawnManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("player_spawn.json", configFile = new JsonConfigPrettyFile()).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public World getSpawnWorld(UUID uuid) {
        try {
            World w = getPlugin().getServer().getWorld(getUUIDEntry(uuid).get("world").getAsString());

            if (w == null) {
                w = getPlugin().getServer().getWorld("world");
                setSpawnWorld(uuid, w);
            }

            return w;
        } catch (Exception e) {
            World w = getPlugin().getServer().getWorld("world");

            setSpawnWorld(uuid, w);
            return w;
        }
    }

    public boolean isSpawnEnabled(UUID uuid) {
        try {
            return getUUIDEntry(uuid).get("enabled").getAsBoolean();
        } catch (Exception e) {
            setSpawnEnabled(uuid, false);
            return false;
        }
    }

    public void setSpawnEnabled(UUID uuid, boolean flag) {
        getUUIDEntry(uuid).set("enabled", flag);
    }

    public void setSpawnWorld(UUID uuid, World w) {
        getUUIDEntry(uuid).set("world", w.getName());
    }

    protected JsonConfigEntry getUUIDEntry(UUID uuid) {
        JsonConfigEntry entry = configFile.getObject(uuid.toString());

        if (entry == null) {
            configFile.set(uuid.toString(), entry = configFile.createEntry());
        }

        return entry;
    }

    protected JsonConfigEntry getLocationEntry(UUID uuid) {
        JsonConfigEntry entry = getUUIDEntry(uuid).getObject("spawn");

        if (entry == null) {
            configFile.set("spawn", entry = configFile.createEntry());
        }

        return entry;
    }

    public Location getSpawnLocation(UUID uuid) {
        try {
            JsonConfigEntry entry = getLocationEntry(uuid);

            double x = entry.get("x").getAsDouble();
            double y = entry.get("y").getAsDouble();
            double z = entry.get("z").getAsDouble();
            float pitch = entry.get("pitch").getAsFloat();
            float yaw = entry.get("yaw").getAsFloat();

            return new Location(getSpawnWorld(uuid), x, y, z, yaw, pitch);
        } catch (Exception e) {
            Location loc = new Location(getSpawnWorld(uuid), 0, 0, 0);
            setSpawnLocation(uuid, loc);

            return loc;
        }
    }

    public void setSpawnLocation(UUID uuid, Location location) {
        setSpawnWorld(uuid, location.getWorld());

        JsonConfigEntry entry = getLocationEntry(uuid);

        entry.set("x", location.getX());
        entry.set("y", location.getY());
        entry.set("z", location.getZ());
        entry.set("pitch", location.getPitch());
        entry.set("yaw", location.getYaw());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (isSpawnEnabled(p.getUniqueId())) {
            e.setRespawnLocation(getSpawnLocation(p.getUniqueId()));
            e.getPlayer().setNoDamageTicks(160);
        }
    }

}