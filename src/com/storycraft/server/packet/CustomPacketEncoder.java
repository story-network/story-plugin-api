package com.storycraft.server.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CustomPacketEncoder extends PacketEncoder {

    private ServerNetworkManager serverNetworkManager;

    private EnumProtocolDirection direction;

    public volatile Player player;

    public CustomPacketEncoder(ServerNetworkManager serverNetworkManager, EnumProtocolDirection direction) {
        super(direction);

        this.direction = direction;
        this.serverNetworkManager = serverNetworkManager;
    }

    public CustomPacketEncoder(ServerNetworkManager serverNetworkManager) {
        this(serverNetworkManager, EnumProtocolDirection.CLIENTBOUND);
    }

    public ServerNetworkManager getServerNetworkManager() {
        return serverNetworkManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) throws Exception {
        Channel channel = ctx.channel();
        EnumProtocol var4 = channel.attr(NetworkManager.c).get();

        if (var4 == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet.toString());
        } else {
            Integer id = var4.a(direction, packet);

            if (id == null) {
                throw new IOException("Can't serialize unregistered packet");
            } else {
                PacketDataSerializer packetDataSerializer = new PacketDataSerializer(buf);
                packetDataSerializer.d(id);

                try {
                    //replace part start
                    handleLoginStart(channel, packet);
                    AsyncPacketOutEvent e = getServerNetworkManager().onPacketOutAsync(player, channel, packet, new DefaultPacketSerializer(packet));

                    if (e.isCancelled())
                        return;

                    e.getSerializer().serialize(packetDataSerializer);
                    //replace part end
                } catch (Throwable var8) {
                    getServerNetworkManager().getPlugin().getLogger().warning(var8.getLocalizedMessage());
                }

            }
        }
    }

    private void handleLoginStart(Channel channel, Object packet) {
        if (packet instanceof PacketLoginInStart) {
            PacketLoginInStart loginPacket = (PacketLoginInStart) packet;

            GameProfile profile = loginPacket.a();
            getServerNetworkManager().getPlayerChannelMap().put(profile.getId(), channel);
        }
    }
}

class DefaultPacketSerializer extends PacketSerializer {

    public DefaultPacketSerializer(Packet packet) {
        super(packet);
    }

    @Override
    protected void serialize(PacketDataSerializer serializer) throws IOException {
        getPacket().b(serializer);
    }
}