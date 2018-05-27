package com.storycraft.server.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.util.HashMap;
import java.util.Map;

public class ClientEntityHandler implements Listener {

    Map<Class<? extends Entity>, Class<? extends Entity>> lookupMap;

    public ClientEntityHandler(){
        lookupMap = new HashMap<>();
    }

    public void initialize(StoryPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void addClientLookup(Class<? extends Entity> entityClass, Class<? extends Entity> clientEntityClass){
        if (contains(entityClass))
            return;

        lookupMap.put(entityClass, clientEntityClass);
    }

    public boolean contains(Class<? extends Entity> entityClass){
        return lookupMap.containsKey(entityClass);
    }

    public Class<? extends Entity> getClientEntityClass(Class<? extends Entity> entityClass){
        return lookupMap.get(entityClass);
    }
}
