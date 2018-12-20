package com.storycraft.core.player.debug;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_13_R2.PacketPlayOutLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UserDebug extends MiniPlugin implements Listener {

    private static final boolean DEFAULT = false;

    private Reflect.WrappedField<Boolean, PacketPlayOutLogin> reducedDebugField;

    private Reflect.WrappedField<MinecraftKey, PacketPlayOutCustomPayload> payloadChannel;
    private Reflect.WrappedField<PacketDataSerializer, PacketPlayOutCustomPayload> dataSerializer;

    public void onLoad(StoryPlugin plugin) {
        this.reducedDebugField = Reflect.getField(PacketPlayOutLogin.class, "h");

        this.payloadChannel = Reflect.getField(PacketPlayOutCustomPayload.class, "i");
        this.dataSerializer = Reflect.getField(PacketPlayOutCustomPayload.class, "j");
    }

    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        patchServerName();
    }

    public void patchServerName() {

    }

    @EventHandler
    public void onPlayerLogin(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutLogin) {
            PacketPlayOutLogin packet = (PacketPlayOutLogin) e.getPacket();

            if (e.getTarget() != null && !e.getTarget().hasPermission("server.play.debug")) {
                reducedDebugField.set(packet, !DEFAULT);
            }

            reducedDebugField.set(packet, DEFAULT);
        }
        else if (e.getPacket() instanceof PacketPlayOutCustomPayload) {
            PacketPlayOutCustomPayload packet = (PacketPlayOutCustomPayload) e.getPacket();

            MinecraftKey key = payloadChannel.get(packet);

            if (PacketPlayOutCustomPayload.b.equals(key)) {
                PacketDataSerializer serializer = dataSerializer.get(packet);

                serializer.a(getPlugin().getServerName());

                dataSerializer.set(packet, serializer);
            }
        }
    }
}
