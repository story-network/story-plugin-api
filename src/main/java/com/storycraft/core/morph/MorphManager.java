package com.storycraft.core.morph;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.morph.entity.IMorphEntity;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import net.minecraft.server.v1_13_R2.DataWatcher.Item;

import net.minecraft.server.v1_13_R2.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MorphManager extends MiniPlugin {

    private List<MorphInfo> morphList;
    private MorphHandler handler;

    private Reflect.WrappedField<Integer, PacketPlayOutEntityMetadata> packetMetadataEidField;

    private WrappedField<List<Item<?>>, PacketPlayOutEntityMetadata> itemListField;

    public MorphManager(){
        this.morphList = new ArrayList<>();

        this.handler = new MorphHandler();

        this.packetMetadataEidField = Reflect.getField(PacketPlayOutEntityMetadata.class, "a");
        this.itemListField = Reflect.getField(PacketPlayOutEntityMetadata.class, "b");
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    public void setMorph(Entity e, IMorphEntity morph) {
        setMorph(new MorphInfo(e, morph));
    }

    public void setMorph(MorphInfo info){
        removeMorph(info.getEntity());

        morphList.add(info);

        //update again
        Packet spawnPacket = EntityPacketUtil.getEntitySpawnPacket(info.getMorph().getNMSEntity());
        
        EntityPacketUtil.setEntityIdPacket(spawnPacket, info.getEntity().getEntityId());

        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), spawnPacket);
        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), EntityPacketUtil.getEntityMetadataPacket(info.getEntity().getEntityId(), info.getMorph().getFixedMetadata(), true));
    }

    public boolean containsEntity(Entity e){
        return getMorphInfo(e) != null;
    }

    private boolean containsEntityInternal(World w, int eid){
        return getMorphInfoInternal(w, eid) != null;
    }

    public MorphInfo getMorphInfo(Entity e){
        return getMorphInfoInternal(e.getWorld(), e.getEntityId());
    }

    private MorphInfo getMorphInfoInternal(World w, int eid){
        for (MorphInfo info : morphList) {
            if (info.getEntity().getWorld().getName().equals(w.getName()) && info.getEntity().getEntityId() == eid) {
                return info;
            }
        }

        return null;
    }

    public void removeMorph(Entity e) {
        MorphInfo info = getMorphInfo(e);

        if (info != null) {
            morphList.remove(info);

            //update
            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), e, EntityPacketUtil.getEntitySpawnPacket(((CraftEntity)e).getHandle()));
            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), e, EntityPacketUtil.getEntityMetadataPacket(((CraftEntity)e).getHandle()));
        }
    }

    private class MorphHandler implements Listener {

        @EventHandler
        public void onEntityPacket(AsyncPacketOutEvent e){
            if (!EntityPacketUtil.isEntitySpawnPacket(e.getPacket()))
                return;

            World w = e.getTarget().getWorld();
            Packet entitySpawnPacket = e.getPacket();
            int eid = EntityPacketUtil.getEntityIdFromPacket(entitySpawnPacket);

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info == null)
                return;

            Entity entity = info.getEntity();
            IMorphEntity morph = info.getMorph();

            Location loc = entity.getLocation();

            morph.getNMSEntity().setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

            if (morph.getNMSEntity() instanceof EntityPlayer){
                sendFakePlayerPacket(e.getTarget(), (EntityPlayer) entity);
            }

            Packet morphPacket = EntityPacketUtil.getEntitySpawnPacket(morph.getNMSEntity());
            EntityPacketUtil.setEntityIdPacket(morphPacket, eid);

            e.setPacket(morphPacket);
        }

        protected void sendFakePlayerPacket(Player p, EntityPlayer... playerEntitys) {
            PacketPlayOutPlayerInfo infoAddPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, playerEntitys);
            PacketPlayOutPlayerInfo infoRemovePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, playerEntitys);

            ConnectionUtil.sendPacket(p, infoAddPacket);
            getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ConnectionUtil.sendPacket(p, infoRemovePacket);
                }
            }, 1);
        }

        @EventHandler
        public void onMetadataPacket(AsyncPacketOutEvent e){
            if (!(e.getPacket() instanceof PacketPlayOutEntityMetadata))
                return;

            World w = e.getTarget().getWorld();
            PacketPlayOutEntityMetadata metadataPacket = (PacketPlayOutEntityMetadata) e.getPacket();

            int eid = packetMetadataEidField.get(metadataPacket);
            List<Item<?>> itemList = itemListField.get(metadataPacket);

            MorphInfo info = getMorphInfoInternal(w, eid);

            if (info == null)
                return;

            //for reupdate
            if (itemList != null) {
                for (Item<?> item : itemList) {
                    item.a(true);
                }
            }

            e.setPacket(EntityPacketUtil.getEntityMetadataPacket(eid, info.getMorph().getFixedMetadata(), true));
        }
    }
}
