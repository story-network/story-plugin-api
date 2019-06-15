package com.storycraft.util;

import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class ConnectionUtil {

    public static final int VIEW_DISTANCE = 256;
	public static Object s;

    public static void sendPacket(Player p, Packet... packets){
        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;

        for (Packet packet : packets)
            connection.sendPacket(packet);
    }

    public static void sendPacket(final Packet... packets){
        Collection<Player> playerList = (Collection<Player>) Bukkit.getOnlinePlayers();

        Parallel.forEach(playerList, (Player p) -> {
            sendPacket(p, packets);
        });
    }

    public static void sendPacketNearby(Location location, double distance, final Packet... packets){
        sendPacketNearbyExcept(location, new ArrayList<>(0), distance, packets);
    }

    public static void sendPacketNearby(Location location, final Packet... packets){
        sendPacketNearby(location, VIEW_DISTANCE, packets);
    }

    public static void sendPacketNearbyExcept(Location location, Entity e, final Packet... packets){
        sendPacketNearbyExcept(location, Lists.newArrayList(e), VIEW_DISTANCE, packets);
    }

    public static void sendPacketNearbyExcept(Location location, List<Entity> list, double distance, final Packet... packets){
        World w = location.getWorld();

        double distanceSq = Math.pow(distance, 2);

        for (Player p : w.getPlayers()) {
            if (p.getLocation().distanceSquared(location) <= distanceSq && !list.contains(p))
                sendPacket(p, packets);
        }
    }
}
