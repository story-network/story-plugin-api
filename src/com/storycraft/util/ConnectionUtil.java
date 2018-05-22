package com.storycraft.util;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ConnectionUtil {

    private static final int VIEW_DISTANCE = 256;

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
        World w = location.getWorld();

        double distanceSq = Math.pow(distance, 2);

        Parallel.forEach(w.getPlayers(), (Player p) -> {
            if (p.getLocation().distanceSquared(location) <= distanceSq)
                sendPacket(p, packets);
        });
    }

    public static void sendPacketNearby(Location location, final Packet... packets){
        sendPacketNearby(location, VIEW_DISTANCE, packets);
    }
}
