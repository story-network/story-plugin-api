package com.storycraft.server.world.addon;

import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.server.world.IWorldAddon;
import com.storycraft.util.BlockIdUtil;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;


public class SnowPickAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(MainPlugin plugin, World world, JsonConfigEntry entry) {
		return new SnowPickAddonHandler(plugin, this, world, entry);
    }
    
    public class SnowPickAddonHandler extends AddonHandler {

        protected SnowPickAddonHandler(MainPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry entry) {
            super(plugin, addon, world, entry);
        }
        
        @EventHandler
        public void onDamage(BlockDamageEvent e) {
            if (isTargetWorld(e.getBlock().getWorld()) && e.getBlock().getType() == Material.SNOW) {
                Snow data = (Snow) e.getBlock().getBlockData();
                int layer = data.getLayers() - 1;

                if (layer < 1) {
                    e.getBlock().setType(Material.AIR);
                } else {
                    data.setLayers(layer);
                    e.getBlock().setBlockData(data);
                }

                e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, BlockIdUtil.getCombinedId(data));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.SNOWBALL));
            }
        }

    }

}