package com.storycraft.server.world.addon;

import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoFallDamageAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(World world) {
		return new NoFallDamageAddonHandler(this, world);
    }
    
    public class NoFallDamageAddonHandler extends AddonHandler {

        protected NoFallDamageAddonHandler(IWorldAddon addon, World world) {
            super(addon, world);
        }
        
        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (isTargetWorld(e.getEntity().getWorld()) && e.getCause() == DamageCause.FALL) {
                e.setCancelled(true);
            }
        }

    }

}