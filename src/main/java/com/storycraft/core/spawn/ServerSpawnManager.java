package com.storycraft.core.spawn;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;


public class ServerSpawnManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("spawn.json", configFile = new JsonConfigFile()).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    protected JsonConfigEntry getLocationEntry() {
        JsonConfigEntry entry = configFile.getObject("location");

        if (entry == null) {
            configFile.set("location", entry = configFile.createEntry());
        }

        return entry;
    }

    public Location getSpawnLocation() {
        try {
            double x = configFile.get("x").getAsDouble();
            double y = configFile.get("y").getAsDouble();
            double z = configFile.get("z").getAsDouble();
            float pitch = configFile.get("pitch").getAsFloat();
            float yaw = configFile.get("yaw").getAsFloat();

            return new Location(getSpawnWorld(), x, y, z, yaw, pitch);
        } catch (Exception e) {
            Location loc = new Location(getSpawnWorld(), 0, 0, 0);
            setSpawnLocation(loc);

            return loc;
        }
    }

    public void setSpawnLocation(Location location) {
        setSpawnWorld(location.getWorld());

        JsonConfigEntry entry = getLocationEntry();

        configFile.set("x", location.getX());
        configFile.set("y", location.getY());
        configFile.set("z", location.getZ());
        configFile.set("pitch", location.getPitch());
        configFile.set("yaw", location.getYaw());
    }

    public World getSpawnWorld() {
        try {
            World w = getPlugin().getServer().getWorld(configFile.get("world").getAsString());

            if (w == null) {
                w = getPlugin().getServer().getWorld("world");
                setSpawnWorld(w);
            }

            return w;
        } catch (Exception e) {
            World w = getPlugin().getServer().getWorld("world");

            setSpawnWorld(w);
            return w;
        }
    }

    public void setSpawnWorld(World w) {
        configFile.set("world", w.getName());
    }

    public boolean isSpawnEnabled() {
        try {
            return configFile.get("enabled").getAsBoolean();
        } catch (Exception e) {
            setSpawnEnabled(false);
            return false;
        }
    }

    public void setSpawnEnabled(boolean flag) {
        configFile.set("enabled", flag);
    }

}