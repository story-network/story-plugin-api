package com.storycraft.server.world.addon;

import com.storycraft.server.world.IWorldAddon;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;

public class NoPhysicsAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(World world) {
		return new NoPhysicsAddonHandler(this, world);
    }
    
    public class NoPhysicsAddonHandler extends AddonHandler {

        protected NoPhysicsAddonHandler(IWorldAddon addon, World world) {
            super(addon, world);
        }
        
        @EventHandler
        public void onPhysics(BlockPhysicsEvent e) {
            if (isTargetWorld(e.getSourceBlock().getWorld()) && !e.getSourceBlock().isLiquid())
                e.setCancelled(true);
        }

    }

}