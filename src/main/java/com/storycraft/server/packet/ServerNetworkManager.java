package com.storycraft.server.packet;

import com.storycraft.MainPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.ServerManager;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageTemplate;
import com.storycraft.util.MessageUtil.MessageType;
import com.storycraft.util.reflect.Reflect;
import io.netty.channel.*;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.*;


public class ServerNetworkManager extends ServerExtension implements Listener {

    private static final String HANDLER_NAME = "story_plugin_handler";

    private ServerManager serverManager;

    private List<Channel> serverChannelList;

    private List<ChannelFuture> channelFutureList;
    private List<NetworkManager> networkManagerList;

    private List<Channel> injectChannelList;
    private Map<String, Channel> playerChannelMap;

    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitializer;
    private ChannelInitializer<Channel> endInitializer;

    private Reflect.WrappedField<List<NetworkManager>, ServerConnection> networkManagerListField;
    private Reflect.WrappedField<List<ChannelFuture>, ServerConnection> channelFutureListField;

    public ServerNetworkManager(ServerManager serverManager) {
        this.serverManager = serverManager;

        this.serverChannelList = new ArrayList<>();

        this.networkManagerList = null;
        this.serverChannelHandler = null;
        this.beginInitializer = null;
        this.endInitializer = null;

        this.injectChannelList = new ArrayList<>();
        this.playerChannelMap = new HashMap<>();

        this.networkManagerListField = Reflect.getField(ServerConnection.class, "g");
        this.channelFutureListField = Reflect.getField(ServerConnection.class, "f");

        hookServerNetwork();
    }

    protected ServerManager getServerManager() {
        return serverManager;
    }

    protected List<Channel> getInjectChannelList() {
        return injectChannelList;
    }

    protected Map<String, Channel> getPlayerChannelMap() {
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
            return networkManagerList = networkManagerListField.get(getServerConnection());

        return networkManagerList;
    }

    protected List<NetworkManager> getNetworkManagerList() {
        return getNetworkManagerList(false);
    }

    protected List<ChannelFuture> getChannelFutureList(boolean update) {
        if (update || channelFutureList == null)
            return channelFutureList = channelFutureListField.get(getServerConnection());

        return channelFutureList;
    }

    protected List<ChannelFuture> getChannelFutureList() {
        return getChannelFutureList(false);
    }

    @Override
    public void onLoad(MainPlugin plugin) {
        
    }

    @Override
    public void onEnable(){
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            injectPlayer(p);
        }

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean reload){
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            uninjectPlayer(p);
        }

        List<ChannelFuture> channelFutureList = getChannelFutureList();

        for (ChannelFuture channelFuture : channelFutureList) {
            Channel channel = channelFuture.channel();

            if (channel.pipeline().get(HANDLER_NAME) != null)
                channel.pipeline().remove(HANDLER_NAME);
        }
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

    private void injectChannelInternal(Channel channel, boolean update) {
        if (update || !getInjectChannelList().contains(channel)) {
            CustomPacketEncoder encoder = new CustomPacketEncoder(this);
            CustomPacketDecoder decoder = new CustomPacketDecoder(this);

            channel.pipeline().replace("decoder", "decoder", decoder);
            channel.pipeline().replace("encoder", "encoder", encoder);

            getInjectChannelList().add(channel);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent e){
        if (!isEnabled())
            return;

        Channel channel = getChannel(e.getPlayer());

        if (getInjectChannelList().contains(channel)) {
            injectPlayer(e.getPlayer());
        }
        else {
            getPlugin().getLogger().warning(e.getPlayer().getName() + " 의 패킷 핸들러 삽입이 실패 했습니다.");
            e.disallow(Result.KICK_OTHER, "Server Handler is not loaded yet");
        }
    }

    private void injectChannelInternal(Channel channel) {
        injectChannelInternal(channel, false);
    }

    public boolean isChannelInjected(Channel channel) {
        return getInjectChannelList().contains(channel);
    }

    public boolean injectChannel(Channel channel) {
        if (isChannelInjected(channel))
            return false;

        injectChannelInternal(channel, true);
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
        if (p == null)
            return false;

        Channel channel = getChannel(p);

        if (channel == null) {
            getPlugin().getLogger().warning("플레이어 " + p.getName() + " 채널에 핸들러를 삽입할 수 없습니다.");
            return false;
        }

        try {
            injectChannel(channel);

            ((CustomPacketEncoder) channel.pipeline().get("encoder")).player = p;
            ((CustomPacketDecoder) channel.pipeline().get("decoder")).player = p;

            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "NetworkManager", "Player Network Hook injected to " + channel.remoteAddress()));
        } catch (Exception e) {
            getPlugin().getLogger().warning("플레이어 " + p.getName() + " 채널에 핸들러를 삽입할 수 없습니다.");
        }

        return true;
    }

    public boolean uninjectPlayer(Player p){
        UUID id = ((CraftPlayer)p).getProfile().getId();

        if (!getPlayerChannelMap().containsKey(id))
            return false;

        Channel channel = getPlayerChannelMap().get(id);

        uninjectChannel(channel);
        return true;
    }

    public Channel getChannel(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        String name = ep.getProfile().getName();

        if (getPlayerChannelMap().containsKey(name))
            return getPlayerChannelMap().get(name);

        try {
            Channel channel = ep.playerConnection.networkManager.channel;
            getPlayerChannelMap().put(name, channel);

            return channel;
        } catch (Exception e) {
            
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