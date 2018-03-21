package com.storycraft.server.packet;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import java.io.IOException;

public abstract class PacketSerializer {

    private Packet packet;

    public PacketSerializer(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    protected abstract void serialize(PacketDataSerializer serializer) throws IOException;
}
