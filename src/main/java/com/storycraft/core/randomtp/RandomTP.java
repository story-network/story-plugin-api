package com.storycraft.core.randomtp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.IConfigEntry;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomTP extends MiniPlugin implements ICommand {

    private static final int TELEPORT_COOLTIME = 10000;

    private JsonConfigFile configFile;

    private Map<UUID, Long> teleportTracker;

    public RandomTP() {
        teleportTracker = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("randomtp.json", configFile = new JsonConfigFile()).run();
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "rtp" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        long time = System.currentTimeMillis() - getPlayerLastTeleport(p);
        if (time <= TELEPORT_COOLTIME) {
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "RandomTP", "다음 사용까지 " + Math.ceil(time / 10) + " 초 기다려야 합니다"));
            return;
        }

        if (args.length > 0) {
            String name = args[0];
            World world = getPlugin().getServer().getWorld(name);

            if (world == null) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "RandomTP", "월드 " + name + " 을(를) 찾을 수 없습니다"));
                return;
            }

            randomTPPlayer(p, world);
            return;
        }

        randomTPPlayer(p, p.getWorld());
    }

    public void randomTPPlayer(Player p, World w) {
        if (!canUse(w)) {
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "RandomTP", "이 월드에서는 랜덤 텔레포트가 비활성화 되어있습니다"));
            return;
        }

        Location center = getRandomTPCenter(w);
        double radius = getRandomTPRadius(w);

        Location location = w.getHighestBlockAt(center.add(radius - (Math.random() * radius * 2), 0, radius - (Math.random() * radius * 2))).getLocation();

        p.teleport(location.add(0, 2, 0));
    }

    private void logPlayerLastTeleport(Player p, long time) {
        if (!teleportTracker.containsKey(p.getUniqueId())) {
            teleportTracker.put(p.getUniqueId(), time);
        }
        else {
            teleportTracker.replace(p.getUniqueId(), time);
        }
    }

    private long getPlayerLastTeleport(Player p) {
        if (!teleportTracker.containsKey(p.getUniqueId())) {
            return 0;
        }
        else {
            return teleportTracker.get(p.getUniqueId());
        }
    }

    protected JsonConfigEntry getEntry(World w) {
        JsonConfigEntry entry = configFile.getObject(w.getName());

        if (entry == null) {
            configFile.set(w.getName(), entry = configFile.createEntry());
        }

        return entry;
    }

    public boolean canUse(World w) {
        JsonConfigEntry entry = getEntry(w);

        try {
            return entry.get("enabled").getAsBoolean();
        } catch (Exception e) {
            setCanUse(w, false);

            return false;
        }
    }

    public void setCanUse(World w, boolean flag) {
        JsonConfigEntry entry = getEntry(w);
        entry.set("enabled", flag);
    }

    public Location getRandomTPCenter(World w) {
        JsonConfigEntry entry = getEntry(w);

        try {
            JsonConfigEntry locEntry = entry.getObject("center");

            double x = locEntry.get("x").getAsDouble();
            double z = locEntry.get("z").getAsDouble();

            return new Location(w, x, 0, z);
        } catch (Exception e) {
            Location defaultLocation = new Location(w, 0, 0, 0);
            setRandomTPCenter(w, defaultLocation);

            return defaultLocation;
        }
    }

    public void setRandomTPCenter(World w, Location location) {
        JsonConfigEntry entry = getEntry(w);
        JsonConfigEntry locEntry = entry.createEntry();

        locEntry.set("x", location.getX());
        locEntry.set("z", location.getZ());

        entry.set("center", locEntry);
    }

    public double getRandomTPRadius(World w) {
        JsonConfigEntry entry = getEntry(w);

        try {
            return entry.get("radius").getAsDouble();
        } catch (Exception e) {
            setRandomTPRadius(w, 3000);

            return 3000;
        }
    }

    public void setRandomTPRadius(World w, double radius) {
        JsonConfigEntry entry = getEntry(w);

        entry.set("radius", radius);
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