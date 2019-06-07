package com.storycraft.server.world.addon;

import com.storycraft.StoryPlugin;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class FixedMobAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(StoryPlugin plugin, World world) {
		return new FixedMobAddonHandler(plugin, this, world);
    }
    
    public class FixedMobAddonHandler extends AddonHandler {

        protected FixedMobAddonHandler(StoryPlugin plugin, IWorldAddon addon, World world) {
            super(plugin, addon, world);
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