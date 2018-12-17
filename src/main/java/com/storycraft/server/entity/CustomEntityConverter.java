package com.storycraft.server.entity;

import java.util.UUID;

import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEntityConverter implements Listener {

    private ServerEntityRegistry serverEntityRegistry;

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

    public CustomEntityConverter(ServerEntityRegistry serverEntityRegistry) {
        this.serverEntityRegistry = serverEntityRegistry;

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


        this.dataWatcherEntityField = Reflect.getField(DataWatcher.class, "c");
    }

    public ServerEntityRegistry getServerEntityRegistry() {
        return serverEntityRegistry;
    }

    @EventHandler
    public void onLivingPacket(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutSpawnEntityLiving) {
            PacketPlayOutSpawnEntityLiving packet = (PacketPlayOutSpawnEntityLiving) e.getPacket();

            CustomEntityInfo info = getServerEntityRegistry().getById(livingEntityTypeIdField.get(packet));
            if (getServerEntityRegistry().containsItem(info)) {
                EntityTypes clientType = info.getClientEntityTypes();

                if (!(info instanceof CustomPlayerInfo)) {
                    livingEntityTypeIdField.set(packet, net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(clientType));
                }
                else {
                    e.setCancelled(true);
                    handleOverridePlayerInternal(packet, e.getTarget(), (CustomPlayerInfo) info);
                }
            }
        }
    }

    private void handleOverridePlayerInternal(PacketPlayOutSpawnEntityLiving living, Player p, CustomPlayerInfo info) {
        DataWatcher watcher = livingMetadataField.get(living);
        Entity e = dataWatcherEntityField.get(watcher);

        PacketPlayOutPlayerInfo fakeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER);
        PacketPlayOutPlayerInfo removeFakeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER);

        PacketPlayOutNamedEntitySpawn playerSpawn = new PacketPlayOutNamedEntitySpawn();

        namedEntityIdField.set(playerSpawn, livingEntityIdField.get(living));
        namedEntityUUIDField.set(playerSpawn, livingEntityUUIDField.get(living));
        namedEntityLocXField.set(playerSpawn, livingEntityLocXField.get(living));
        namedEntityLocYField.set(playerSpawn, livingEntityLocYField.get(living));
        namedEntityLocZField.set(playerSpawn, livingEntityLocZField.get(living));
        namedEntityYawField.set(playerSpawn, livingEntityYawField.get(living));
        namedEntityPitchField.set(playerSpawn, livingEntityPitchField.get(living));
        namedMetadataField.set(playerSpawn, livingMetadataField.get(living));

        ConnectionUtil.sendPacket(p, fakeInfo, playerSpawn, removeFakeInfo);
    }


}
