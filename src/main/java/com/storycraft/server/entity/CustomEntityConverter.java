package com.storycraft.server.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.storycraft.MainPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumGamemode;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IRegistry;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.PacketPlayOutStatistic;
import net.minecraft.server.v1_14_R1.Statistic;
import net.minecraft.server.v1_14_R1.StatisticList;

public class CustomEntityConverter implements Listener {

    private ServerEntityRegistry serverEntityRegistry;

    private Reflect.WrappedField<Integer, PacketPlayOutSpawnEntity> entityTypeIdField;

    private Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> livingEntityIdField;
    private Reflect.WrappedField<UUID, PacketPlayOutSpawnEntityLiving> livingEntityUUIDField;
    private Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> livingEntityTypeIdField;
    private Reflect.WrappedField<Double, PacketPlayOutSpawnEntityLiving> livingEntityLocXField;
    private Reflect.WrappedField<Double, PacketPlayOutSpawnEntityLiving> livingEntityLocYField;
    private Reflect.WrappedField<Double, PacketPlayOutSpawnEntityLiving> livingEntityLocZField;
    private Reflect.WrappedField<Byte, PacketPlayOutSpawnEntityLiving> livingEntityYawField;
    private Reflect.WrappedField<Byte, PacketPlayOutSpawnEntityLiving> livingEntityPitchField;

    private Reflect.WrappedField<DataWatcher, PacketPlayOutSpawnEntityLiving> livingMetadataField;

    private Reflect.WrappedField<Integer, PacketPlayOutNamedEntitySpawn> namedEntityIdField;
    private Reflect.WrappedField<UUID, PacketPlayOutNamedEntitySpawn> namedEntityUUIDField;
    private Reflect.WrappedField<Double, PacketPlayOutNamedEntitySpawn> namedEntityLocXField;
    private Reflect.WrappedField<Double, PacketPlayOutNamedEntitySpawn> namedEntityLocYField;
    private Reflect.WrappedField<Double, PacketPlayOutNamedEntitySpawn> namedEntityLocZField;
    private Reflect.WrappedField<Byte, PacketPlayOutNamedEntitySpawn> namedEntityYawField;
    private Reflect.WrappedField<Byte, PacketPlayOutNamedEntitySpawn> namedEntityPitchField;

    private Reflect.WrappedField<DataWatcher, PacketPlayOutNamedEntitySpawn> namedMetadataField;

    private Reflect.WrappedField<? extends Entity, DataWatcher> dataWatcherEntityField;

    private Reflect.WrappedField<List<Object>, PacketPlayOutPlayerInfo> infoDataListField;

    private Reflect.WrappedField<Object2IntMap<Statistic<?>>, PacketPlayOutStatistic> statisticMap;

    private Class playerInfoDataClass;
    private Reflect.WrappedConstructor<Object> playerInfoDataConstructor;

    public CustomEntityConverter(ServerEntityRegistry serverEntityRegistry) {
        this.serverEntityRegistry = serverEntityRegistry;

        this.entityTypeIdField = Reflect.getField(PacketPlayOutSpawnEntity.class, "k");

        this.livingEntityIdField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "a");
        this.livingEntityUUIDField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "b");
        this.livingEntityTypeIdField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "c");
        this.livingEntityLocXField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "d");
        this.livingEntityLocYField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "e");
        this.livingEntityLocZField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "f");
        this.livingEntityYawField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "j");
        this.livingEntityPitchField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "k");
        this.livingMetadataField = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "m");

        this.namedEntityIdField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "a");
        this.namedEntityUUIDField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "b");
        this.namedEntityLocXField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "c");
        this.namedEntityLocYField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "d");
        this.namedEntityLocZField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "e");
        this.namedEntityYawField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "f");
        this.namedEntityPitchField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "g");
        this.namedMetadataField = Reflect.getField(PacketPlayOutNamedEntitySpawn.class, "h");

        this.dataWatcherEntityField = Reflect.getField(DataWatcher.class, "entity");

        this.infoDataListField = Reflect.getField(PacketPlayOutPlayerInfo.class, "b");

        this.statisticMap = Reflect.getField(PacketPlayOutStatistic.class, "a");

        try {
            this.playerInfoDataClass = Class
                    .forName("net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo$PlayerInfoData");

            this.playerInfoDataConstructor = Reflect.getConstructor(playerInfoDataClass, PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ServerEntityRegistry getServerEntityRegistry() {
        return serverEntityRegistry;
    }

    @EventHandler
    public void onEntityPacket(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutSpawnEntityLiving) {
            PacketPlayOutSpawnEntityLiving packet = (PacketPlayOutSpawnEntityLiving) e.getPacket();
            int id = livingEntityTypeIdField.get(packet);

            if (!getServerEntityRegistry().containsNetworkId(id))
                return;

            CustomEntityInfo info = getServerEntityRegistry().getByNetworkId(id);
            EntityTypes clientType = info.getClientEntityTypes();

            if (!(info instanceof CustomPlayerInfo)) {
                livingEntityTypeIdField.set(packet, net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.a(clientType));
            }
            else {
                e.setCancelled(true);
                handleOverridePlayerInternal(packet, e.getTarget(), (CustomPlayerInfo) info);
            }
        }
        else if (e.getPacket() instanceof PacketPlayOutStatistic) {
            PacketPlayOutStatistic packet = (PacketPlayOutStatistic) e.getPacket();

            Map<Statistic<?>, Integer> map = statisticMap.get(packet);

            for (Statistic<?> stat : new ArrayList<>(map.keySet())) {
                if (StatisticList.ENTITY_KILLED.equals(stat.getWrapper()) || StatisticList.ENTITY_KILLED_BY.equals(stat.getWrapper())) {
                    EntityTypes type = (EntityTypes) stat.b();
                    if (getServerEntityRegistry().contains(IRegistry.ENTITY_TYPE.getKey(type).getKey())) {
                        map.remove(stat);
                    }
                }
            }
        }
    }

    private void handleOverridePlayerInternal(PacketPlayOutSpawnEntityLiving living, Player p, CustomPlayerInfo info) {
        DataWatcher watcher = livingMetadataField.get(living);
        Entity e = dataWatcherEntityField.get(watcher);

        PacketPlayOutPlayerInfo fakeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
        GameProfile profile = info.getProfileHandler().getProfile(e);
        Object playerInfoDataAdd = playerInfoDataConstructor.createNew(fakeInfo, profile, 0, EnumGamemode.NOT_SET, new ChatComponentText(""));

        infoDataListField.get(fakeInfo).add(playerInfoDataAdd);

        PacketPlayOutPlayerInfo removeFakeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER);
        Object playerInfoDataRemove = playerInfoDataConstructor.createNew(removeFakeInfo, profile, 0, EnumGamemode.NOT_SET, new ChatComponentText(""));

        infoDataListField.get(removeFakeInfo).add(playerInfoDataRemove);

        PacketPlayOutNamedEntitySpawn playerSpawn = new PacketPlayOutNamedEntitySpawn();

        namedEntityIdField.set(playerSpawn, livingEntityIdField.get(living));
        namedEntityUUIDField.set(playerSpawn, livingEntityUUIDField.get(living));
        namedEntityLocXField.set(playerSpawn, livingEntityLocXField.get(living));
        namedEntityLocYField.set(playerSpawn, livingEntityLocYField.get(living));
        namedEntityLocZField.set(playerSpawn, livingEntityLocZField.get(living));
        namedEntityYawField.set(playerSpawn, livingEntityYawField.get(living));
        namedEntityPitchField.set(playerSpawn, livingEntityPitchField.get(living));
        namedMetadataField.set(playerSpawn, livingMetadataField.get(living));

        ConnectionUtil.sendPacket(p, fakeInfo, playerSpawn);

        MainPlugin plugin = getServerEntityRegistry().getRegistryManager().getPlugin();

        getServerEntityRegistry().getRegistryManager().runSync(() -> {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                ConnectionUtil.sendPacket(p, removeFakeInfo);
            }, 1);
            
            return null;
        });
    }


}
