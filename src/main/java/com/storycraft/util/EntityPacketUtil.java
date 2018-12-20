package com.storycraft.util;

import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_13_R2.*;

public class EntityPacketUtil {

    private static Reflect.WrappedMethod<Packet, EntityTrackerEntry> entitySpawnMethod;

    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntity> entityIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> entityLivingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityExperienceOrb> entityExpIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityPainting> entityPaintingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityWeather> entityWeatherIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutNamedEntitySpawn> entityPlayerIdField;

    public static Packet getEntitySpawnPacket(Entity entity) {
        EntityTrackerEntry entry = new EntityTrackerEntry(entity, 0, 0, 0, true);

        if (entitySpawnMethod == null)
            entitySpawnMethod = Reflect.getMethod(EntityTrackerEntry.class, "e");

        return entitySpawnMethod.invoke(entry);
    }

    public static int getEntityIdFromPacket(Packet spawnPacket){
        if (spawnPacket instanceof PacketPlayOutSpawnEntity){
            if (entityIdField == null)
                entityIdField = Reflect.getField(PacketPlayOutSpawnEntity.class, "a");

            return entityIdField.get((PacketPlayOutSpawnEntity) spawnPacket);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityLiving){
            if (entityLivingIdField == null)
                entityLivingIdField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "a");

            return entityLivingIdField.get((PacketPlayOutSpawnEntityLiving) spawnPacket);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityExperienceOrb){
            if (entityExpIdField == null)
                entityExpIdField = Reflect.getField(PacketPlayOutSpawnEntityExperienceOrb.class, "a");

            return entityExpIdField.get((PacketPlayOutSpawnEntityExperienceOrb) spawnPacket);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityPainting){
            if (entityPaintingIdField == null)
                entityPaintingIdField = Reflect.getField(PacketPlayOutSpawnEntityPainting.class, "a");

            return entityPaintingIdField.get((PacketPlayOutSpawnEntityPainting) spawnPacket);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityWeather){
            if (entityWeatherIdField == null)
                entityWeatherIdField = Reflect.getField(PacketPlayOutSpawnEntityWeather.class, "a");

            return entityWeatherIdField.get((PacketPlayOutSpawnEntityWeather) spawnPacket);
        } else if (spawnPacket instanceof PacketPlayOutNamedEntitySpawn){
            if (entityPlayerIdField == null)
                entityPlayerIdField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "a");

            return entityPlayerIdField.get((PacketPlayOutNamedEntitySpawn) spawnPacket);
        }

        return 0;
    }

    public static void setEntityIdPacket(Packet spawnPacket, int id){
        if (spawnPacket instanceof PacketPlayOutSpawnEntity){
            if (entityIdField == null)
                entityIdField = Reflect.getField(PacketPlayOutSpawnEntity.class, "a");

             entityIdField.set((PacketPlayOutSpawnEntity) spawnPacket, id);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityLiving){
            if (entityLivingIdField == null)
                entityLivingIdField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "a");

            entityLivingIdField.set((PacketPlayOutSpawnEntityLiving) spawnPacket, id);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityExperienceOrb){
            if (entityExpIdField == null)
                entityExpIdField = Reflect.getField(PacketPlayOutSpawnEntityExperienceOrb.class, "a");

            entityExpIdField.set((PacketPlayOutSpawnEntityExperienceOrb) spawnPacket, id);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityPainting){
            if (entityPaintingIdField == null)
                entityPaintingIdField = Reflect.getField(PacketPlayOutSpawnEntityPainting.class, "a");

            entityPaintingIdField.set((PacketPlayOutSpawnEntityPainting) spawnPacket, id);
        } else if (spawnPacket instanceof PacketPlayOutSpawnEntityWeather){
            if (entityWeatherIdField == null)
                entityWeatherIdField = Reflect.getField(PacketPlayOutSpawnEntityWeather.class, "a");

            entityWeatherIdField.set((PacketPlayOutSpawnEntityWeather) spawnPacket, id);
        } else if (spawnPacket instanceof PacketPlayOutNamedEntitySpawn){
            if (entityPlayerIdField == null)
                entityPlayerIdField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "a");

            entityPlayerIdField.set((PacketPlayOutNamedEntitySpawn) spawnPacket, id);
        }
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
