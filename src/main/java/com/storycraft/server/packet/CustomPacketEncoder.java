package com.storycraft.server.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import net.minecraft.server.v1_13_R1.*;
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
            //replace part start
            PacketDataSerializer packetDataSerializer = new PacketDataSerializer(buf);
            try {
                AsyncPacketOutEvent e = getServerNetworkManager().onPacketOutAsync(player, channel, packet, DefaultPacketSerializer.getInstance());

                Integer id = var4.a(direction, e.getPacket());

                if (id == null) {
                    throw new IOException("Can't serialize unregistered packet");
                }

                if (e.isCancelled())
                    return;

                packetDataSerializer.d(id);
                e.getSerializer().serialize(e.getPacket(), packetDataSerializer);
                //replace part end
            } catch (Throwable var8) {
                getServerNetworkManager().getPlugin().getLogger().warning(var8.getLocalizedMessage());
            }
        }
    }
}

class DefaultPacketSerializer extends PacketSerializer {

    private static DefaultPacketSerializer instance;

    static {
        instance = new DefaultPacketSerializer();
    }

    public static DefaultPacketSerializer getInstance() {
        return instance;
    }

    private DefaultPacketSerializer() {

    }

    @Override
    protected void serialize(Packet packet, PacketDataSerializer serializer) throws IOException {
        packet.b(serializer);
    }
}