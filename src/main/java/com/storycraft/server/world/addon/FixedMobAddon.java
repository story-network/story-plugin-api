package com.storycraft.server.world.addon;

import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class FixedMobAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(MainPlugin plugin, World world, JsonConfigEntry entry) {
		return new FixedMobAddonHandler(plugin, this, world, entry);
    }
    
    public class FixedMobAddonHandler extends AddonHandler {

        protected FixedMobAddonHandler(MainPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry entry) {
            super(plugin, addon, world, entry);
        }
        
        @EventHandler
        public void onSpawn(EntitySpawnEvent e) {
            if (isTargetWorld(e.getEntity().getWorld())) {
                e.getEntity().setSilent(true);
                e.getEntity().setPersistent(true);

                if (e.getEntity() instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) e.getEntity();
                    living.setAI(false);
                    living.setCanPickupItems(false);
                    living.setCollidable(false);
                }
            }
        }

        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (isTargetWorld(e.getEntity().getWorld())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onDamageByPlayer(EntityDamageByEntityEvent e) {
            if (isTargetWorld(e.getEntity().getWorld()) && e.getDamager() instanceof Player) {
                e.getEntity().remove();
            }
        }

    }

}