package com.storycraft.util;

import java.util.Map;

import com.storycraft.util.reflect.Reflect;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import net.minecraft.server.v1_13_R2.*;
import net.minecraft.server.v1_13_R2.PacketPlayOutMultiBlockChange.MultiBlockChangeInfo;

public class PacketUtil {

    private static Reflect.WrappedMethod<Packet, EntityTrackerEntry> entitySpawnMethod;

    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntity> entityIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> entityLivingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityExperienceOrb> entityExpIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityPainting> entityPaintingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityWeather> entityWeatherIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutNamedEntitySpawn> entityPlayerIdField;

    private static Reflect.WrappedField<IBlockData, PacketPlayOutBlockChange> blockUpdateBlockDataField;

    private static Reflect.WrappedField<ChunkCoordIntPair, PacketPlayOutMultiBlockChange> multiBlockUpdateChunkField;
    private static Reflect.WrappedField<MultiBlockChangeInfo[], PacketPlayOutMultiBlockChange> multiBlockUpdateInfoField;

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

    public static Packet getEntityMetadataPacket(int id, DataWatcher watcher, boolean flag) {
        return new PacketPlayOutEntityMetadata(id, watcher, flag);
    }

    public static Packet getEntityDestroyPacket(Entity e) {
        return new PacketPlayOutEntityDestroy(e.getId());
    }

    public static PacketPlayOutBlockChange getBlockUpdatePacket(Location loc, BlockData data) {
        if (blockUpdateBlockDataField == null) {
            blockUpdateBlockDataField = Reflect.getField(PacketPlayOutBlockChange.class, "a");
        }

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        blockUpdateBlockDataField.set(packet, BlockIdUtil.getNMSBlockData(data));

        return packet;
    }

    public static PacketPlayOutMultiBlockChange getMultiBlockUpdatePacket(Chunk chunk, Map<Location, BlockData> dataMap) {
        if (multiBlockUpdateChunkField == null) {
            multiBlockUpdateChunkField = Reflect.getField(PacketPlayOutMultiBlockChange.class, "a");
        }

        if (multiBlockUpdateInfoField == null) {
            multiBlockUpdateInfoField = Reflect.getField(PacketPlayOutMultiBlockChange.class, "b");
        }

        PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] infoList = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[dataMap.size()];

        PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange();

        int i = 0;

        for (Location loc : dataMap.keySet()) {
            if (loc.getChunk().equals(chunk)) {
                IBlockData data = BlockIdUtil.getNMSBlockData(dataMap.get(loc));

                short relPos = 0;
                relPos = (short) ((loc.getBlockX() - chunk.getX() * 16) << 12); //X
                relPos = (short) loc.getBlockY();                               //Y
                relPos = (short) ((loc.getBlockZ() - chunk.getZ() * 16) << 8);  //Z

                infoList[i++] = packet.new MultiBlockChangeInfo(relPos, data);
            }
        }

        PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] list = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[i];

        for (int j = 0;j < i; j++) {
            list[j] = infoList[j];
        }

        multiBlockUpdateChunkField.set(packet, new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
        multiBlockUpdateInfoField.set(packet, list);

        return packet;
    }

    public static boolean isEntitySpawnPacket(Packet packet){
        return packet instanceof PacketPlayOutSpawnEntity || packet instanceof PacketPlayOutSpawnEntityLiving || packet instanceof PacketPlayOutSpawnEntityExperienceOrb
                || packet instanceof PacketPlayOutSpawnEntityPainting || packet instanceof PacketPlayOutSpawnEntityWeather || packet instanceof PacketPlayOutNamedEntitySpawn;
    }

    public static boolean isPlayerSpawnPacket(Packet packet){
        return packet instanceof PacketPlayOutNamedEntitySpawn;
    }
}
