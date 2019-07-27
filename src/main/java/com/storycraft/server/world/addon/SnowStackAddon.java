package com.storycraft.server.world.addon;

import java.util.ArrayList;
import java.util.List;

import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.server.event.server.ServerSyncUpdateEvent;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.server.world.IWorldAddon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SnowStackAddon implements IWorldAddon {

    @Override
    public AddonHandler createHandler(MainPlugin plugin, World world, JsonConfigEntry entry) {
		return new SnowStackAddonHandler(plugin, this, world, entry);
    }

    public class SnowStackAddonHandler extends AddonHandler {

        protected SnowStackAddonHandler(MainPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry entry) {
            super(plugin, addon, world, entry);
        }
        
        @EventHandler
        public void onUpdate(ServerSyncUpdateEvent e) {
            if (getWorld().hasStorm()) {
                for (Player p : getWorld().getPlayers()) {

                    List<Chunk> nearbyChunks = getNearbyChunk(p, 128);
                    for (Chunk c : nearbyChunks) {
                        int section = (int) (Math.random() * 15.9);

                        Block b = c.getBlock((int) (Math.random() * 15.9), (int) (section * 16 + Math.random() * 15.9), (int) (Math.random() * 15.9));
    
                        if (b.getTemperature() < 0.15 && b.getType() == Material.SNOW && b.getY() >= getWorld().getHighestBlockYAt(b.getX(), b.getZ())) {
                            Snow snowBlock = (Snow) b.getBlockData();

                            Location loc = b.getLocation();
                            if (loc.getBlockY() < 1 || loc.add(0, -1, 0).getBlock().getType() == Material.SNOW || loc.add(0, -1, 0).getBlock().getType() == Material.SNOW_BLOCK)
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
            }
        }

        protected List<Chunk> getNearbyChunk(Player p, double distance) {
            List<Chunk> list = new ArrayList<>();

            Location center = p.getLocation();
            int chunkDistance = (int) distance / 16;
            double distanceSq = Math.pow(distance, 2);

            for (int oX = -chunkDistance; oX <= chunkDistance; oX++) {
                for (int oZ = -chunkDistance; oZ <= chunkDistance; oZ++) {
                    Location loc = center.clone().add(oX * 16, 0, oZ * 16);

                    if (loc.distanceSquared(center) <= distanceSq)
                        list.add(loc.getChunk());
                }
            }

            return list;
        }
    }
}