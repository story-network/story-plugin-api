package com.storycraft.server.packet;

import io.netty.channel.Channel;
import net.minecraft.server.v1_13_R1.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncPacketInEvent extends AsyncPacketEvent {

    private static final HandlerList handlers = new HandlerList();

    private Player sender;
    private PacketDeserializer deserializer;

    public AsyncPacketInEvent(Packet packet, Channel channel, Player sender, PacketDeserializer deserializer) {
        super(PacketDirection.CLIENT_TO_SERVER, channel, packet);

        this.sender = sender;
        this.deserializer = deserializer;
    }

    public Player getSender() {
        return sender;
    }

    public PacketDeserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(PacketDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
