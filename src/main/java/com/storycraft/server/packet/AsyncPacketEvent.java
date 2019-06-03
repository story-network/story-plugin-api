package com.storycraft.server.packet;

import io.netty.channel.Channel;
import net.minecraft.server.v1_14_R1.Packet;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPacketEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private PacketDirection direction;
    private Packet packet;
    private Channel channel;

    public AsyncPacketEvent(PacketDirection direction, Channel channel, Packet packet) {
        super(true);
        this.direction = direction;
        this.channel = channel;
        this.packet = packet;

        this.cancelled = false;
    }

    public PacketDirection getDirection() {
        return direction;
    }

    public Channel getChannel() {
        return channel;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }
}
