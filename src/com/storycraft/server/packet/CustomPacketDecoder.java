package com.storycraft.server.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import net.minecraft.server.v1_13_R1.*;
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
                DefaultPacketDeserializer defaultDeserializer = DefaultPacketDeserializer.getInstance();

                defaultDeserializer.deserialize(packet, packetDataSerializer);
                handleLoginStart(channel, packet);

                AsyncPacketInEvent e = getServerNetworkManager().onPacketInAsync(player, channel, packet, defaultDeserializer);

                if (e.isCancelled())
                    return;

                if (e.getPacket() != packet || e.getDeserializer() != defaultDeserializer)
                    e.getDeserializer().deserialize(packet, packetDataSerializer);
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

            GameProfile profile = loginPacket.b();
            getServerNetworkManager().getPlayerChannelMap().put(profile.getName(), channel);
        }
    }
}

class DefaultPacketDeserializer extends PacketDeserializer {

    private static DefaultPacketDeserializer instance;

    static {
        instance = new DefaultPacketDeserializer();
    }

    public static DefaultPacketDeserializer getInstance(){
        return instance;
    }

    private DefaultPacketDeserializer() {

    }

    @Override
    protected void deserialize(Packet packet, PacketDataSerializer serializer) throws IOException {
        packet.a(serializer);
    }
}
