package com.storycraft.server.forge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.server.packet.PacketSerializer;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class ForgeServerListPing implements Listener {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ServerPing.ServerData.class, new ServerPing.ServerData.Serializer())
            .registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing.ServerPingPlayerSample.Serializer())
            .registerTypeAdapter(ForgeServerPing.class, new ForgeServerPing.Serializer()) //replaced part
            .registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer())
            .registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer())
            .registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();

    private Reflect.WrappedField<ServerPing, PacketStatusOutServerInfo> serverPingField;

    private ForgeServerManager forgeServerManager;

    public ForgeServerListPing(ForgeServerManager forgeServerManager) {
        this.forgeServerManager = forgeServerManager;

        getForgeServerManager().getPlugin().getServer().getPluginManager().registerEvents(this, getForgeServerManager().getPlugin());

        serverPingField = Reflect.getField(PacketStatusOutServerInfo.class, "b");
    }

    public ForgeServerManager getForgeServerManager() {
        return forgeServerManager;
    }

    @EventHandler
    public void onListRefresh(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketStatusOutServerInfo) {
            ForgeServerPing forgeServerPing = new ForgeServerPing(serverPingField.get((PacketStatusOutServerInfo) e.getPacket()));

            e.setSerializer(new ForgeListPingSerializer(forgeServerPing));
        }
    }

    private class ForgeListPingSerializer extends PacketSerializer {

        private ForgeServerPing forgeServerPing;

        public ForgeListPingSerializer(ForgeServerPing forgeServerPing) {
            this.forgeServerPing = forgeServerPing;
        }

        public ForgeServerPing getForgeServerPing() {
            return forgeServerPing;
        }

        @Override
        protected void serialize(Packet packet, PacketDataSerializer serializer) throws IOException {
            serializer.a(gson.toJson(getForgeServerPing()));
        }
    }
}
