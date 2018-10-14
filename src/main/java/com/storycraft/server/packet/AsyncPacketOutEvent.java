package com.storycraft.server.packet;

import io.netty.channel.Channel;
import net.minecraft.server.v1_13_R2.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPacketOutEvent extends AsyncPacketEvent {

    private static final HandlerList handlers = new HandlerList();

    private Player target;
    private PacketSerializer serializer;

    public AsyncPacketOutEvent(Packet packet, Channel channel, Player target, PacketSerializer serializer) {
        super(PacketDirection.SERVER_TO_CLIENT, channel, packet);

        this.target = target;
        this.serializer = serializer;
    }

    public Player getTarget() {
        return target;
    }

    public PacketSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(PacketSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
