package com.storycraft.server.packet;

import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import java.io.IOException;

public abstract class PacketDeserializer {

    public PacketDeserializer() {

    }

    protected abstract void deserialize(Packet packet, PacketDataSerializer serializer) throws IOException;
}
