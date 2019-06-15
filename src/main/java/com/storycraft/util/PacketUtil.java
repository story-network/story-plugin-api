package com.storycraft.util;

import java.util.List;
import java.util.Map;

import com.storycraft.util.reflect.Reflect;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import net.minecraft.server.v1_14_R1.*;
import net.minecraft.server.v1_14_R1.PacketPlayOutMultiBlockChange.MultiBlockChangeInfo;

public class PacketUtil {

    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntity> entityIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> entityLivingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityExperienceOrb> entityExpIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityPainting> entityPaintingIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityWeather> entityWeatherIdField;
    private static Reflect.WrappedField<Integer, PacketPlayOutNamedEntitySpawn> entityPlayerIdField;

    private static Reflect.WrappedField<ChunkCoordIntPair, PacketPlayOutMultiBlockChange> multiBlockUpdateChunkField;
    private static Reflect.WrappedField<MultiBlockChangeInfo[], PacketPlayOutMultiBlockChange> multiBlockUpdateInfoField;

    private static Reflect.WrappedField<int[], PacketPlayOutMount> passengerEidListField;

    private static Reflect.WrappedField<int[], PacketPlayOutEntityDestroy> destroyedEntityEidList;

    public static Packet getEntitySpawnPacket(Entity entity) {
        return entity.N();
    }

    public static int[] getEntityDestroyList(PacketPlayOutEntityDestroy destroy) {
        if (destroyedEntityEidList == null) {
            destroyedEntityEidList  = Reflect.getField(PacketPlayOutEntityDestroy.class, "a");
        }

        return destroyedEntityEidList.get(destroy);
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
        return getEntityMetadataPacket(e, false);
    }

    public static Packet getEntityMetadataPacket(Entity e, boolean flag) {
        return getEntityMetadataPacket(e, e.getDataWatcher(), flag);
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

    public static PacketPlayOutMount getEntityMountPacket(Entity e) {
        return new PacketPlayOutMount(e);
    }

    public static PacketPlayOutMount getEntityMountPacket(Entity e, Entity... passengerList) {
        int[] list = new int[passengerList.length];

        for (int i = 0; i < list.length; i++) {
            list[i] = passengerList[i].getId();
        }

        return getEntityMountPacket(e, list);
    }

    public static PacketPlayOutMount getEntityMountPacket(Entity e, List<Entity> passengerList) {
        int[] list = new int[passengerList.size()];

        for (int i = 0; i < list.length; i++) {
            list[i] = passengerList.get(i).getId();
        }

        return getEntityMountPacket(e, list);
    }
    
    public static PacketPlayOutMount getEntityMountPacket(Entity e, int... passengerEidList) {
        if (passengerEidListField == null) {
            passengerEidListField = Reflect.getField(PacketPlayOutMount.class, "b");
        }

        PacketPlayOutMount mount = getEntityMountPacket(e);

        passengerEidListField.set(mount, passengerEidList);

        return mount;
    }

    public static PacketPlayOutBlockChange getBlockUpdatePacket(Location loc, BlockData data) {

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        packet.block = BlockIdUtil.getNMSBlockData(data);

        return packet;
    }

    public static PacketPlayOutMultiBlockChange getMultiBlockUpdatePacket(org.bukkit.World world, int chunkX, int chunkZ, Map<Location, BlockData> dataMap) {
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
            int locChunkX = loc.getBlockX() >> 4;
            int locChunkZ = loc.getBlockZ() >> 4;

            if (loc.getWorld() != null && loc.getWorld().equals(world) && locChunkX == chunkX && locChunkZ == chunkZ) {
                IBlockData data = BlockIdUtil.getNMSBlockData(dataMap.get(loc));

                short relPos = 0;
                relPos = (short) ((loc.getBlockX() - chunkX * 16) << 12); //X
                relPos = (short) loc.getBlockY();                               //Y
                relPos = (short) ((loc.getBlockZ() - chunkZ * 16) << 8);  //Z

                infoList[i++] = packet.new MultiBlockChangeInfo(relPos, data);
            }
        }

        PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] list = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[i];

        for (int j = 0;j < i; j++) {
            list[j] = infoList[j];
        }

        multiBlockUpdateChunkField.set(packet, new ChunkCoordIntPair(chunkX, chunkZ));
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
