package com.storycraft.server.packet;

import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;

import java.io.IOException;

public abstract class PacketSerializer {

    public PacketSerializer() {

    }

    protected abstract void serialize(Packet packet, PacketDataSerializer serializer) throws IOException;
}
