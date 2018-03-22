package com.storycraft.server.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class CustomPacketDecoder extends PacketDecoder {

    private ServerNetworkManager serverNetworkManager;

    private EnumProtocolDirection direction;

    public volatile Player player;

    public CustomPacketDecoder(ServerNetworkManager serverNetworkManager, EnumProtocolDirection direction) {
        super(direction);

        this.direction = direction;
        this.serverNetworkManager = serverNetworkManager;
    }

    public CustomPacketDecoder(ServerNetworkManager serverNetworkManager) {
        this(serverNetworkManager, EnumProtocolDirection.SERVERBOUND);
    }

    public ServerNetworkManager getServerNetworkManager() {
        return serverNetworkManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        if (buf.readableBytes() != 0) {
            Channel channel = ctx.channel();
            PacketDataSerializer packetDataSerializer = new PacketDataSerializer(buf);

            int id = packetDataSerializer.g();

            Packet packet = ((EnumProtocol)channel.attr(NetworkManager.c).get()).a(direction, id);

            if (packet == null) {
                throw new IOException("Bad packet id " + id);
            } else {
                //replace part start
                DefaultPacketDeserializer defaultDeserializer = new DefaultPacketDeserializer(packet);

                defaultDeserializer.deserialize(packetDataSerializer);

                AsyncPacketInEvent e = getServerNetworkManager().onPacketInAsync(player, channel, packet, defaultDeserializer);

                if (e.isCancelled())
                    return;

                if (e.getDeserializer() != defaultDeserializer)
                    e.getDeserializer().deserialize(packetDataSerializer);
                //replace part end

                if (packetDataSerializer.readableBytes() > 0) {
                    throw new IOException("Packet " + channel.attr(NetworkManager.c).get().a() + "/" + id + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetDataSerializer.readableBytes() + " bytes extra whilst reading packet " + id);
                } else {
                    list.add(packet);
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

class DefaultPacketDeserializer extends PacketDeserializer {

    public DefaultPacketDeserializer(Packet packet) {
        super(packet);
    }

    @Override
    protected void deserialize(PacketDataSerializer serializer) throws IOException {
        getPacket().a(serializer);
    }
}
