package com.storycraft.server.world;

import com.storycraft.StoryPlugin;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.WorldServer;

public interface IWorldAddon {

    AddonHandler createHandler(StoryPlugin plugin, World world);

    public class AddonHandler implements Listener {

        private StoryPlugin plugin;
        private IWorldAddon addon;
        private World world;

        protected AddonHandler(StoryPlugin plugin, IWorldAddon addon, World world) {
            this.plugin = plugin;
            this.addon = addon;
            this.world = world;
        }

        public IWorldAddon getAddon() {
            return addon;
        }

        protected World getWorld() {
            return world;
        }

        protected CraftWorld getCraftWorld() {
            return (CraftWorld)world;
        }

        public StoryPlugin getPlugin() {
            return plugin;
        }

        protected WorldServer getNMSWorld() {
            return getCraftWorld().getHandle();
        }

        public boolean isTargetWorld(World w) {
            return getWorld().equals(w);
        }
    }
}