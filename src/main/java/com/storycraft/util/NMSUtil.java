package com.storycraft.util;

import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.World;

public class NMSUtil {

    public static EntityPlayer getNMSPlayer(Player p) {
        return (EntityPlayer) getNMSEntity(p);
    }

    public static Entity getNMSEntity(org.bukkit.entity.Entity e) {
        return ((CraftEntity) e).getHandle();
    }

    public static EntityLiving getNMSEntityLiving(org.bukkit.entity.LivingEntity e) {
        return (EntityLiving) getNMSEntity(e);
    }

    public static World getNMSWorld(org.bukkit.World w) {
        return ((CraftWorld) w).getHandle();
    }
}