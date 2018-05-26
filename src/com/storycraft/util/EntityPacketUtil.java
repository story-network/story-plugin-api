package com.storycraft.util;

import net.minecraft.server.v1_12_R1.*;

public class EntityPacketUtil {
    public static Packet getEntitySpawnPacket(Entity entity){
        EntityTrackerEntry entry = new EntityTrackerEntry(entity, 0, 0, 0, true);

        return Reflect.invokeMethod(entry, "e");
    }

    public static Packet getEntityMetadataPacket(Entity e) {
        return getEntityMetadataPacket(e, e.getDataWatcher(), false);
    }

    public static Packet getEntityMetadataPacket(Entity e, DataWatcher watcher, boolean flag) {
        return new PacketPlayOutEntityMetadata(e.getId(), watcher, flag);
    }

    public static Packet getEntityDestroyPacket(Entity e) {
        return new PacketPlayOutEntityDestroy(e.getId());
    }

    public static boolean isEntitySpawnPacket(Packet packet){
        return packet instanceof PacketPlayOutSpawnEntity || packet instanceof PacketPlayOutSpawnEntityLiving || packet instanceof PacketPlayOutSpawnEntityExperienceOrb
                || packet instanceof PacketPlayOutSpawnEntityPainting || packet instanceof PacketPlayOutSpawnEntityWeather || packet instanceof PacketPlayOutNamedEntitySpawn;
    }

    public static boolean isPlayerSpawnPacket(Packet packet){
        return packet instanceof PacketPlayOutNamedEntitySpawn;
    }
}
