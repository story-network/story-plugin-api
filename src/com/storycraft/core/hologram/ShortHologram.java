package com.storycraft.core.hologram;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class ShortHologram extends Hologram {

    public ShortHologram(Location location, String... texts) {
        super(location, texts);
    }

    @Override
    protected Entity createHologramEntity(int line) {
        EntityAreaEffectCloud areaEffectCloud = new EntityAreaEffectCloud(((CraftWorld)getLocation().getWorld()).getHandle(), getLocation().getX(), getLocation().getY() - line * 0.5d, getLocation().getZ());

        areaEffectCloud.setCustomNameVisible(true);
        areaEffectCloud.setRadius(0);
        areaEffectCloud.setInvisible(true);

        return areaEffectCloud;
    }
}
