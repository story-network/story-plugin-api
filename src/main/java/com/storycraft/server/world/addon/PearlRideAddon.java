package com.storycraft.server.world.addon;

import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;


public class PearlRideAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(MainPlugin plugin, World world, JsonConfigEntry entry) {
		return new PearlRideAddonHandler(plugin, this, world, entry);
    }
    
    public class PearlRideAddonHandler extends AddonHandler {

        protected PearlRideAddonHandler(MainPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry entry) {
            super(plugin, addon, world, entry);
        }
        
        @EventHandler
        public void onThrow(ProjectileLaunchEvent e) {
            if (e.getEntity() instanceof EnderPearl && isTargetWorld(e.getEntity().getWorld())) {
                EnderPearl pearl = (EnderPearl) e.getEntity();

                if (pearl.getShooter() instanceof LivingEntity) {
                    LivingEntity living = ((LivingEntity)pearl.getShooter());
                    
                    living.leaveVehicle();
                    pearl.addPassenger(living);
                    pearl.setShooter(null);
                }
            }
        }

    }

}