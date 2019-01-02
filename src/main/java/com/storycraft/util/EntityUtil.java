package com.storycraft.util;

import net.minecraft.server.v1_13_R2.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.EntityType;

public class EntityUtil {

    public static Entity createNMSEntity(World w, EntityType type) {
        return createNMSEntity(new Location(w, 0, 0, 0), type);
    }

    public static Entity createNMSEntity(Location loc, EntityType type) {
        CraftWorld cw = (CraftWorld) loc.getWorld();

        return cw.createEntity(loc, type.getEntityClass());
    }

}
