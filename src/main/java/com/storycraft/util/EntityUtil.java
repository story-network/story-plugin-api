package com.storycraft.util;

import net.minecraft.server.v1_14_R1.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class EntityUtil {

    public static <T extends Entity>T createNMSEntity(World w, EntityType type) {
        return createNMSEntity(new Location(w, 0, 0, 0), type);
    }

    public static <T extends Entity>T createNMSEntity(Location loc, EntityType type) {
        CraftWorld cw = (CraftWorld) loc.getWorld();

        return (T) cw.createEntity(loc, type.getEntityClass());
    }

}
