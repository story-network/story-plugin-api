package com.storycraft.server.world.addon;

import com.storycraft.StoryPlugin;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoFallDamageAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(StoryPlugin plugin, World world) {
		return new NoFallDamageAddonHandler(plugin, this, world);
    }
    
    public class NoFallDamageAddonHandler extends AddonHandler {

        protected NoFallDamageAddonHandler(StoryPlugin plugin, IWorldAddon addon, World world) {
            super(plugin, addon, world);
        }
        
        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (isTargetWorld(e.getEntity().getWorld()) && e.getCause() == DamageCause.FALL) {
                e.setCancelled(true);
            }
        }

    }

}