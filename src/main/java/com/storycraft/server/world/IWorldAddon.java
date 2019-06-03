package com.storycraft.server.world;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.WorldServer;

public interface IWorldAddon {

    AddonHandler createHandler(World world);

    public class AddonHandler implements Listener {

        private IWorldAddon addon;
        private World world;

        protected AddonHandler(IWorldAddon addon, World world) {
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

        protected WorldServer getNMSWorld() {
            return getCraftWorld().getHandle();
        }

        public boolean isTargetWorld(World w) {
            return getWorld().equals(w);
        }
    }
}