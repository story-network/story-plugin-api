package com.storycraft.core.spawn;

import java.util.ArrayList;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class ServerSpawnManager extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("spawn.json", configFile = new JsonConfigPrettyFile()).run();

        plugin.getCommandManager().addCommand(new SpawnCommand());
        plugin.getCommandManager().addCommand(new SetSpawnCommand());
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() == null)
            return;

        if (isInSpawn(e.getEntity().getLocation()) && !getCanBlockInteract()) {
            if (e.getEntity() instanceof Player) {
                if (getCanPlayerIgnoreDamage())
                    e.setCancelled(true);
            }
            else {
                if (getCanEntityIgnoreDamage())
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockDestroyed(BlockBreakEvent e) {
        if (e.getBlock() != null && e.getPlayer() != null && isInSpawn(e.getBlock().getLocation()) && !getCanBlockInteract() && !e.getPlayer().hasPermission("server.spawn.admin.block"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamaged(BlockDamageEvent e) {
        if (e.getBlock() != null && e.getPlayer() != null && isInSpawn(e.getBlock().getLocation()) && !getCanBlockInteract() && !e.getPlayer().hasPermission("server.spawn.admin.block"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        if (e.getBlock() != null && e.getPlayer() != null && isInSpawn(e.getBlock().getLocation()) && !getCanBlockInteract() && !e.getPlayer().hasPermission("server.spawn.admin.block"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onFirstJoin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPlayedBefore() && isSpawnEnabled()) {
            p.teleport(getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (isSpawnEnabled()) {
            e.setRespawnLocation(getSpawnLocation());
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        if (getCanBlockInteract())
            return;

        for (Block b : new ArrayList<>(e.blockList())) {
            if (b != null && isInSpawn(b.getLocation()))
                e.blockList().remove(b);
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent e) {
        if (isInSpawn(e.getBlock().getLocation()) && !getCanBlockInteract())
            e.setCancelled(true);
    }

    public boolean isInSpawn(Location location) {
        if (!isSpawnEnabled() || location == null || !location.getWorld().equals(getSpawnWorld()))
            return false;
        
        Location loc = getSpawnLocation();

        loc.setY(location.getY());

        return location.distanceSquared(loc) <= Math.pow(getSpawnRadius(), 2);
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
            JsonConfigEntry entry = getLocationEntry();

            double x = entry.get("x").getAsDouble();
            double y = entry.get("y").getAsDouble();
            double z = entry.get("z").getAsDouble();
            float pitch = entry.get("pitch").getAsFloat();
            float yaw = entry.get("yaw").getAsFloat();

            return new Location(getSpawnWorld(), x, y, z, yaw, pitch);
        } catch (Exception e) {
            Location loc = new Location(getSpawnWorld(), 0, 0, 0);
            setSpawnLocation(loc);
            setSpawnRadius(16);
            setCanBlockInteract(false);

            return loc;
        }
    }

    public void setSpawnLocation(Location location) {
        setSpawnWorld(location.getWorld());

        JsonConfigEntry entry = getLocationEntry();

        entry.set("x", location.getX());
        entry.set("y", location.getY());
        entry.set("z", location.getZ());
        entry.set("pitch", location.getPitch());
        entry.set("yaw", location.getYaw());
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

    public double getSpawnRadius() {
        try {
            return configFile.get("radius").getAsDouble();
        } catch (Exception e) {
            setSpawnRadius(0);

            return 0;
        }
    }

    public void setCanBlockInteract(boolean flag) {
        configFile.set("can_interact_block", flag);
    }

    public boolean getCanBlockInteract() {
        try {
            return configFile.get("can_interact_block").getAsBoolean();
        } catch (Exception e) {
            setCanBlockInteract(false);

            return false;
        }
    }

    public void setCanPlayerIgnoreDamage(boolean flag) {
        configFile.set("player_ignore_damage", flag);
    }

    public boolean getCanPlayerIgnoreDamage() {
        try {
            return configFile.get("player_ignore_damage").getAsBoolean();
        } catch (Exception e) {
            setCanPlayerIgnoreDamage(false);

            return false;
        }
    }

    public void setCanEntityIgnoreDamage(boolean flag) {
        configFile.set("entity_ignore_damage", flag);
    }

    public boolean getCanEntityIgnoreDamage() {
        try {
            return configFile.get("entity_ignore_damage").getAsBoolean();
        } catch (Exception e) {
            setCanEntityIgnoreDamage(false);

            return false;
        }
    }

    public void setSpawnRadius(double radius) {
        configFile.set("radius", radius);
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

    public class SpawnCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "spawn", "lobby", "hub", "l" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Player p = (Player) sender;

            if (!isSpawnEnabled()) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "SpawnManager", "스폰이 비활성화 되어 있습니다"));
                return;
            }

            p.teleport(getSpawnLocation());
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "SpawnManager", "스폰으로 이동되었습니다"));
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
            return "server.command.spawn";
        }
    }

    public class SetSpawnCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "setspawn" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Player p = (Player) sender;

            Location location = p.getLocation();

            setSpawnEnabled(true);
            setSpawnLocation(location);
            setSpawnRadius(16);
            setCanBlockInteract(false);
            setCanPlayerIgnoreDamage(false);
            setCanEntityIgnoreDamage(false);

            p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "SpawnManager", "스폰 위치가 " + location.getWorld().getName() + " " + (Math.floor(location.getX() * 10) / 10) + " " + (Math.floor(location.getY() * 10) / 10) + " " + (Math.floor(location.getZ() * 10) / 10) + " 로 지정되었습니다."));
        }

        @Override
        public boolean isPermissionRequired() {
	        return true;
        }

        @Override
        public String getPermissionRequired() {
            return "server.command.setspawn";
        }

        @Override
        public boolean availableOnConsole() {
            return false;
        }

        @Override
        public boolean availableOnCommandBlock() {
            return false;
		}
    }

}