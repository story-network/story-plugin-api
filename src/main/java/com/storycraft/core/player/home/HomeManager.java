package com.storycraft.core.player.home;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;
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
        plugin.getConfigManager().addConfigFile("home.json", homeConfigFile = new JsonConfigFile()).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.isCancelled() || !(e.getClickedBlock().getBlockData() instanceof Bed))
            return;

        Player player = e.getPlayer();
        Location location = e.getPlayer().getLocation();

        setRespawnLocation(player, location);
        player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "HomeManager", "스폰 위치가 " + location.getWorld().getName() + " " + (Math.floor(location.getX() * 100) / 100) + " " + (Math.floor(location.getY() * 100) / 100) + " " + (Math.floor(location.getZ() * 100) / 100) + " 로 지정되었습니다."));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Location respawnLocation = getRespawnLocation(e.getPlayer());

        if (respawnLocation != null)
            e.setRespawnLocation(respawnLocation);
    }

    public Location getRespawnLocation(Player p) {
        JsonConfigEntry entry;

        if (!homeConfigFile.contains(p.getUniqueId().toString()) || (entry = homeConfigFile.getObject(p.getUniqueId().toString())) == null)
            return null;

        try {
            return new Location(getPlugin().getServer().getWorld(entry.get("world").getAsString()), entry.get("x").getAsDouble(), entry.get("y").getAsDouble(), entry.get("z").getAsDouble(), entry.get("pitch").getAsFloat(), entry.get("yaw").getAsFloat());
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

}
