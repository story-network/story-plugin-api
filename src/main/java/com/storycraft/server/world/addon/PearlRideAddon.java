package com.storycraft.server.world.addon;

import com.storycraft.StoryPlugin;
import com.storycraft.server.world.IWorldAddon;
import com.storycraft.util.BlockIdUtil;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;


public class PearlRideAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(StoryPlugin plugin, World world) {
		return new PearlRideAddonHandler(plugin, this, world);
    }
    
    public class PearlRideAddonHandler extends AddonHandler {

        protected PearlRideAddonHandler(StoryPlugin plugin, IWorldAddon addon, World world) {
            super(plugin, addon, world);
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