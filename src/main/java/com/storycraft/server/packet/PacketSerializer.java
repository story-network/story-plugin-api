package com.storycraft.server.packet;

import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;

import java.io.IOException;

public abstract class PacketSerializer {

    public PacketSerializer() {

    }

    protected abstract void serialize(Packet packet, PacketDataSerializer serializer) throws IOException;
}
