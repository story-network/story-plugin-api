package com.storycraft.server.packet;

import com.storycraft.StoryPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.ServerManager;
import com.storycraft.util.Reflect;
import io.netty.channel.*;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;


public class ServerNetworkManager extends ServerExtension {

    private static final String HANDLER_NAME = "story_plugin_handler";

    private ServerManager serverManager;

    private List<Channel> serverChannelList;

    private List<ChannelFuture> channelFutureList;
    private List<NetworkManager> networkManagerList;

    private List<Channel> injectChannelList;
    private Map<UUID, Channel> playerChannelMap;

    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitializer;
    private ChannelInitializer<Channel> endInitializer;

    public ServerNetworkManager(ServerManager serverManager) {
        this.serverManager = serverManager;

        this.serverChannelList = new ArrayList<>();

        this.networkManagerList = null;
        this.serverChannelHandler = null;
        this.beginInitializer = null;
        this.endInitializer = null;

        this.injectChannelList = new ArrayList<>();
        this.playerChannelMap = new HashMap<>();
    }

    protected ServerManager getServerManager() {
        return serverManager;
    }

    protected List<Channel> getInjectChannelList() {
        return injectChannelList;
    }

    protected Map<UUID, Channel> getPlayerChannelMap() {
        return playerChannelMap;
    }

    protected ServerConnection getServerConnection(){
        return getServerManager().getMinecraftServer().getServerConnection();
    }

    protected List<Channel> getServerChannelList() {
        return serverChannelList;
    }

    protected List<NetworkManager> getNetworkManagerList(boolean update) {
        if (update || networkManagerList == null)
            return networkManagerList = Reflect.getField(getServerConnection(), "h");

        return networkManagerList;
    }

    protected List<NetworkManager> getNetworkManagerList() {
        return getNetworkManagerList(false);
    }

    protected List<ChannelFuture> getChannelFutureList(boolean update) {
        if (update || channelFutureList == null)
            return channelFutureList = Reflect.getField(getServerConnection(), "g");

        return channelFutureList;
    }

    protected List<ChannelFuture> getChannelFutureList() {
        return getChannelFutureList(false);
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        hookServerNetwork();
    }

    protected AsyncPacketInEvent onPacketInAsync(Player p, Channel channel, Packet packet, PacketDeserializer deserializer) {
        AsyncPacketInEvent packetInEvent = new AsyncPacketInEvent(packet, channel, p, deserializer);

        getPlugin().getServer().getPluginManager().callEvent(packetInEvent);
        return packetInEvent;
    }

    protected AsyncPacketOutEvent onPacketOutAsync(Player p, Channel channel, Packet packet, PacketSerializer serializer) {
        AsyncPacketOutEvent packetOutEvent = new AsyncPacketOutEvent(packet, channel, p, serializer);

        getPlugin().getServer().getPluginManager().callEvent(packetOutEvent);
        return packetOutEvent;
    }

    private void hookServerNetwork() {
        List<ChannelFuture> channelFutureList = getChannelFutureList();

        beginInitializer = new ChannelBeginInitializer();
        endInitializer = new ChannelEndInitializer();
        serverChannelHandler = new ServerChannelHandler();

        for (ChannelFuture channelFuture : channelFutureList) {
            Channel channel = channelFuture.channel();

            if (channel.pipeline().get(HANDLER_NAME) != null)
                channel.pipeline().remove(HANDLER_NAME);

            channel.pipeline().addFirst(HANDLER_NAME, serverChannelHandler);
            getServerChannelList().add(channel);
        }
    }

    private CustomPacketEncoder injectChannelInternal(Channel channel, boolean update) {
        try {
            if (update || !getInjectChannelList().contains(channel)) {
                CustomPacketEncoder handler = new CustomPacketEncoder(this);

                channel.pipeline().replace("decoder", "decoder", new CustomPacketDecoder(this));
                channel.pipeline().replace("encoder", "encoder", new CustomPacketEncoder(this));

                getInjectChannelList().add(channel);

                return handler;
            }
        } catch (Exception e) {
            getPlugin().getLogger().warning("Failed to inject channel " + e.getLocalizedMessage());
        }

        return null;
    }

    private CustomPacketEncoder injectChannelInternal(Channel channel) {
        return injectChannelInternal(channel, false);
    }

    public boolean isChannelInjected(Channel channel) {
        return getInjectChannelList().contains(channel);
    }

    public boolean injectChannel(Channel channel) {
        if (isChannelInjected(channel) || injectChannelInternal(channel, true) == null)
            return false;

        return true;
    }

    public boolean uninjectChannel(Channel channel) {
        if (!isChannelInjected(channel))
            return false;

        uninjectChannelInternal(channel);

        return true;
    }

    private void uninjectChannelInternal(Channel channel) {
        if (channel.pipeline().get(HANDLER_NAME) != null) {
            channel.pipeline().remove(HANDLER_NAME);

            getInjectChannelList().remove(channel);
        }
    }

    public boolean injectPlayer(Player p){
        Channel channel = getChannel(p);

        CustomPacketEncoder packetHandler = injectChannelInternal(channel);

        if (channel == null || packetHandler == null)
            return false;

        injectChannelInternal(channel).player = p;

        return true;
    }

    public boolean uninjectPlayer(Player p){
        if (!getPlayerChannelMap().containsKey(p.getUniqueId()))
            return false;

        Channel channel = getPlayerChannelMap().get(p.getUniqueId());

        uninjectChannel(channel);
        return true;
    }

    public Channel getChannel(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        UUID uuid = ep.getProfile().getId();

        if (getPlayerChannelMap().containsKey(uuid))
            return getPlayerChannelMap().get(p);

        try {
            Channel channel = ep.playerConnection.networkManager.channel;
            getPlayerChannelMap().put(uuid, channel);

            return channel;
        } catch (Exception e) {
            getPlugin().getLogger().warning("플레이어 " + p.getName() + " 채널을 찾을 수 없습니다. 로그인된 플레이어가 맞나요?");
        }

        return null;
    }

    private class ChannelBeginInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast(endInitializer);
        }
    }

    private class ChannelEndInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel channel) throws Exception {
            try {
                synchronized (getNetworkManagerList()) {
                    if (isEnabled()) {
                        channel.eventLoop().submit(() -> injectChannelInternal(channel, true));
                    }
                }
            } catch (Exception e) {
                getPlugin().getLogger().warning("Failed to hook Server Channel " + e.getLocalizedMessage());
            }
        }
    }

    private class ServerChannelHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Channel channel = (Channel) msg;

            channel.pipeline().addFirst(beginInitializer);
            ctx.fireChannelRead(msg);
        }

    }

}