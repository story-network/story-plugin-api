package com.storycraft.server.world.addon;

import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoFallDamageAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(MainPlugin plugin, World world, JsonConfigEntry entry) {
		return new NoFallDamageAddonHandler(plugin, this, world, entry);
    }
    
    public class NoFallDamageAddonHandler extends AddonHandler {

        protected NoFallDamageAddonHandler(MainPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry entry) {
            super(plugin, addon, world, entry);
        }
        
        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (isTargetWorld(e.getEntity().getWorld()) && e.getCause() == DamageCause.FALL) {
                e.setCancelled(true);
            }
        }

    }

}