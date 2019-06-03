package com.storycraft.core.player.debug;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.RankUpdateEvent;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_14_R1.PacketPlayOutLogin;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.netty.buffer.Unpooled;

public class UserDebug extends MiniPlugin implements Listener {

    private static final boolean DEFAULT = false;

    private Reflect.WrappedField<Boolean, PacketPlayOutLogin> reducedDebugField;

    public void onLoad(StoryPlugin plugin) {
        this.reducedDebugField = Reflect.getField(PacketPlayOutLogin.class, "h");
    }

    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
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
    }

    @EventHandler
    public void onRankChange(RankUpdateEvent e) {
        PacketPlayOutEntityStatus packet;

        EntityPlayer ep = ((CraftPlayer)e.getPlayer()).getHandle();
        byte status;

        if (e.getPlayer().hasPermission("server.play.debug")) {
            status = 0x23;
        }
        else {
            status = 0x22;
        }

        packet = new PacketPlayOutEntityStatus(ep, status);

        ConnectionUtil.sendPacket(e.getPlayer(), packet);
    }
}
