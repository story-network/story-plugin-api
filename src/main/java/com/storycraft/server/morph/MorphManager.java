package com.storycraft.server.morph;

import java.util.ArrayList;
import java.util.List;

import com.storycraft.MainMiniPlugin;
import com.storycraft.server.entity.EntityPacketListenerAbstract;
import com.storycraft.server.entity.IEntityHandler;
import com.storycraft.server.morph.entity.IMorphEntity;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

import net.minecraft.server.v1_14_R1.DataWatcher.Item;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;

public class MorphManager extends MainMiniPlugin {

    private List<MorphEntityListener> morphList;

    private WrappedField<List<Item<?>>, PacketPlayOutEntityMetadata> itemListField;

    public MorphManager(){
        this.morphList = new ArrayList<>();

        this.itemListField = Reflect.getField(PacketPlayOutEntityMetadata.class, "b");
    }

    @Override
    public void onEnable(){
        for (MorphEntityListener listener : new ArrayList<>(morphList)) {
            getPlugin().getServer().getPluginManager().registerEvents(listener, getPlugin());
        }
    }

    @Override
    public void onDisable(boolean restart) {
        for (MorphEntityListener listener : new ArrayList<>(morphList)) {
            removeMorph(listener.getInfo().getEntity(), true);
        }
    }

    public void setMorph(Entity e, IMorphEntity morph) {
        setMorph(new MorphInfo(e, morph));
    }

    public void setMorph(IMorphInfo info){
        removeMorph(info.getEntity());
        setMorphInternal(info);
    }

    protected void setMorphInternal(IMorphInfo info){
        MorphEntityListener listener = new MorphEntityListener(info);

        morphList.add(listener);

        //update again
        Packet destroyPacket = new PacketPlayOutEntityDestroy(info.getEntity().getEntityId());

        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), destroyPacket);

        Packet spawnPacket = PacketUtil.getEntitySpawnPacket(info.getMorph().getNMSEntity());
        
        PacketUtil.setEntityIdPacket(spawnPacket, info.getEntity().getEntityId());

        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), spawnPacket);
        ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), PacketUtil.getEntityMetadataPacket(((CraftEntity)info.getEntity()).getHandle(), true));
    }

    public boolean containsEntity(Entity e){
        return getMorphInfo(e) != null;
    }

    private boolean containsEntityInternal(World w, int eid){
        return getMorphInfoInternal(w, eid) != null;
    }

    public IMorphInfo getMorphInfo(Entity e){
        return getMorphInfoInternal(e.getWorld(), e.getEntityId());
    }

    private IMorphInfo getMorphInfoInternal(World w, int eid){
        for (MorphEntityListener listener : morphList) {
            IMorphInfo info = listener.getInfo();

            if (info.getEntity().getWorld().getName().equals(w.getName()) && info.getEntity().getEntityId() == eid) {
                return info;
            }
        }

        return null;
    }

    protected MorphEntityListener getListener(IMorphInfo info) {
        for (MorphEntityListener listener : morphList) {
            if (listener.getInfo() == info)
                return listener;
        }

        return null;
    }

    public void removeMorph(Entity e) {
        removeMorph(e, e.isValid());
    }

    public void removeMorph(Entity e, boolean needRespawn) {
        removeMorphInternal(e, needRespawn);
    }

    protected void removeMorphInternal(Entity e, boolean needRespawn) {
        IMorphInfo info = getMorphInfo(e);

        if (info != null) {
            //update
            Packet destroyPacket = new PacketPlayOutEntityDestroy(info.getEntity().getEntityId());

            ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), destroyPacket);

            MorphEntityListener listener = getListener(info);

            HandlerList.unregisterAll(listener);

            morphList.remove(listener);

            if (needRespawn) {
                ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), PacketUtil.getEntitySpawnPacket(((CraftEntity)e).getHandle()));
                ConnectionUtil.sendPacketNearbyExcept(info.getEntity().getLocation(), info.getEntity(), PacketUtil.getEntityMetadataPacket(((CraftEntity)e).getHandle(), true));
            }
        }
    }

    protected class MorphEntityListener extends EntityPacketListenerAbstract {

        private IMorphInfo info;

        public MorphEntityListener(IMorphInfo info) {
            this.info = info;
        }

        public IMorphInfo getInfo() {
            return info;
        }

        @Override
        public Entity getEntity() {
            return getInfo().getEntity();
        }

        @Override
        public IEntityHandler getHandler() {
            return getInfo().getMorph();
        }

        @Override
        protected void onEntitySpawnPacket(AsyncPacketOutEvent e, int eid) {
            Packet p = PacketUtil.getEntitySpawnPacket(getInfo().getMorph().getNMSEntity());
            PacketUtil.setEntityIdPacket(p, eid);

            e.setPacket(p);
            super.onEntitySpawnPacket(e, eid);
        }
        
        @Override
        protected void onEntityMetadataPacket(AsyncPacketOutEvent e) {
            List<Item<?>> itemList = itemListField.get((PacketPlayOutEntityMetadata) e.getPacket());
            //for reupdate
            if (itemList != null) {
                for (Item<?> item : itemList) {
                    item.a(true);
                }
            }

            e.setPacket(PacketUtil.getEntityMetadataPacket(getEntity().getEntityId(), getInfo().getMorph().getFixedMetadata(), true));

            super.onEntityMetadataPacket(e);
        }
    }
}
