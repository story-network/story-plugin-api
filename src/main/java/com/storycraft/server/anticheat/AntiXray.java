package com.storycraft.server.anticheat;

import com.storycraft.MainPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;

public class AntiXray extends ServerExtension implements Listener {

    private WrappedField<byte[], PacketPlayOutMapChunk> chunkDataField;
    private WrappedField<Integer, PacketPlayOutMapChunk> sectionMaskField;

    @Override
    public void onLoad(MainPlugin plugin) {
        chunkDataField = Reflect.getField(PacketPlayOutMapChunk.class, "e");
        sectionMaskField = Reflect.getField(PacketPlayOutMapChunk.class, "c");
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean restart) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMapChunk(AsyncPacketOutEvent e) {
        if (!(e.getPacket() instanceof PacketPlayOutMapChunk) || e.isCancelled() || e.getTarget() == null || !e.getTarget().hasPermission(getBypassPermission()))
            return;

        PacketPlayOutMapChunk chunkPacket = (PacketPlayOutMapChunk) e.getPacket();

        Chunk c = null;

        int sectionMask = sectionMaskField.get(chunkPacket);
        byte[] data = chunkDataField.get(chunkPacket);

        int i = 0;
        for (int offset = 0; offset < 16; offset++) {
            if (((sectionMask >> offset) & 1) == 1) {
                byte bitsPerBlock = data[i++];

                if (bitsPerBlock <= 4) {

                } else if (bitsPerBlock < 9) {

                } else {

                }
            }
        }
    }

    public String getBypassPermission() {
        return "server.anticheat.xray.bypass";
    }
}