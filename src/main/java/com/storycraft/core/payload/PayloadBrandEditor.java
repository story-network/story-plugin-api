package com.storycraft.core.payload;

import com.storycraft.MiniPlugin;
import com.storycraft.StoryPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutCustomPayload;

public class PayloadBrandEditor extends MiniPlugin implements Listener {

    private Reflect.WrappedField<MinecraftKey, PacketPlayOutCustomPayload> payloadChannel;
    private Reflect.WrappedField<PacketDataSerializer, PacketPlayOutCustomPayload> dataSerializer;

    @Override
    public void onLoad(StoryPlugin plugin) {
        this.payloadChannel = Reflect.getField(PacketPlayOutCustomPayload.class, "n");
        this.dataSerializer = Reflect.getField(PacketPlayOutCustomPayload.class, "o");

        getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerBrandSend(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutCustomPayload) {
            PacketPlayOutCustomPayload packet = (PacketPlayOutCustomPayload) e.getPacket();

            MinecraftKey key = payloadChannel.get(packet);

            if (PacketPlayOutCustomPayload.a.equals(key)) {
                PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());

                serializer.a(getPlugin().getServerName() + ChatColor.RESET);

                dataSerializer.set(packet, serializer);
            }
        }
    }
}