package com.storycraft.test;

import com.mojang.authlib.GameProfile;
import com.storycraft.StoryPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.EntityPacketUtil;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.UUID;

public class TestFunction implements Listener {

    private StoryPlugin plugin;

    public TestFunction(StoryPlugin plugin){
        this.plugin = plugin;
    }

    public static void test(StoryPlugin plugin, org.bukkit.World world) {
        plugin.getServer().getPluginManager().registerEvents(new TestFunction(plugin), plugin);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e){
        World w = ((CraftWorld)e.getLocation().getWorld()).getHandle();
        EntityPlayer entity = new EntityPlayer(w.getMinecraftServer(), (WorldServer) w, new GameProfile(UUID.randomUUID(), "storycraft"), new PlayerInteractManager(w));

        entity.glowing = true;

        plugin.getDecorator().getMorphManager().setMorphToEntity(((CraftEntity)e.getEntity()).getHandle(), entity);
    }
}