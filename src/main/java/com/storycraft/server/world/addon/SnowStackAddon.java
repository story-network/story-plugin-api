package com.storycraft.server.world.addon;

import com.storycraft.StoryPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;

public class SnowStackAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(StoryPlugin plugin, World world) {
		return new SnowStackAddonHandler(plugin, this, world);
    }

    public class SnowStackAddonHandler extends AddonHandler {

        protected SnowStackAddonHandler(StoryPlugin plugin, IWorldAddon addon, World world) {
            super(plugin, addon, world);
        }
        
        @EventHandler
        public void onUpdate(ServerUpdateEvent e) {
            if (getWorld().hasStorm()) {
                getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                    Chunk cList[] = getWorld().getLoadedChunks();
                    int limit = getPlugin().getServer().getOnlinePlayers().size();
                    
                    for (int a = 0; a < limit; a++) {
                        Chunk c = cList[(int) (Math.random() * cList.length)];
                        int section = (int) (Math.random() * 15.9);

                        for (int i = 0; i < 3; i++) {
                            Block b = c.getBlock((int) (Math.random() * 15.9), (int) (section * 16 + Math.random() * 15.9), (int) (Math.random() * 15.9));
    
                            if (b.getTemperature() < 0.15 && b.getType() == Material.SNOW && b.getY() == getWorld().getHighestBlockYAt(b.getX(), b.getZ())) {
                                Snow snowBlock = (Snow) b.getBlockData();
    
                                Location loc = b.getLocation();
                                if (loc.getBlockY() < 2 || loc.add(0, -1, 0).getBlock().getType() == Material.SNOW && loc.add(0, -1, 0).getBlock().getType() == Material.SNOW)
                                    continue;
    
                                if (snowBlock.getLayers() >= snowBlock.getMaximumLayers()) {
                                    b.getLocation().add(0, 1, 0).getBlock().setType(Material.SNOW);
                                }
                                else {
                                    snowBlock.setLayers(snowBlock.getLayers() + 1);
                                    b.setBlockData(snowBlock);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}