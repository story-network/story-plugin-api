package com.storycraft.server.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ClientEntityHandler implements Listener {

    Map<Integer, Class<? extends Entity>> lookupMap;

    public ClientEntityHandler(){
        lookupMap = new HashMap<>();
    }

    public void initialize(StoryPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void addClientLookup(int id, Class<? extends Entity> clientEntityClass){
        if (contains(id))
            return;

        lookupMap.put(id, clientEntityClass);
    }

    public boolean contains(int id){
        return lookupMap.containsKey(id);
    }

    public Class<? extends Entity> getClientEntityClass(int id){
        return lookupMap.get(id);
    }

    @EventHandler
    public void onEntityPacket(AsyncPacketOutEvent e){
        if (!EntityPacketUtil.isEntitySpawnPacket(e.getPacket()) || EntityPacketUtil.isPlayerSpawnPacket(e.getPacket()))
            return;

        int typeId;
        if (e.getPacket() instanceof PacketPlayOutSpawnEntity){
            typeId = Reflect.getField(e.getPacket(), "k");
        }
        else if (e.getPacket() instanceof PacketPlayOutSpawnEntityLiving){
            typeId = Reflect.getField(e.getPacket(), "c");
        }
        else{
            return;
        }

        if (!contains(typeId))
            return;

        Class<? extends Entity> clientEntityClass = getClientEntityClass(typeId);
    }
}
