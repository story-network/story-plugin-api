package com.storycraft.server.hologram;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.*;

public class SimpleInteractableHologram extends Hologram {
    public SimpleInteractableHologram(Location location, String... texts) {
        super(location, texts);
    }

    @Override
    protected Entity createHologramEntity(int line) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld)getLocation().getWorld()).getHandle(), getLocation().getX(), getLocation().getY() - line * 0.25d, getLocation().getZ());

        stand.setCustomNameVisible(true);
        stand.setSmall(true);
        stand.setNoGravity(true);
        stand.setInvisible(true);

        return stand;
    }
}
