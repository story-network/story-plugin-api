package com.storycraft.core.hologram;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class SimpleHologram extends Hologram {
    public SimpleHologram(Location location, String... texts) {
        super(location, texts);
    }

    @Override
    protected Entity createHologramEntity(int line) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld)getLocation().getWorld()).getHandle(), getLocation().getX(), getLocation().getY() - 0.4875d - line * 0.25d, getLocation().getZ());

        stand.setCustomNameVisible(true);
        stand.setSmall(true);
        stand.setNoGravity(true);
        stand.setInvisible(true);

        return stand;
    }
}
