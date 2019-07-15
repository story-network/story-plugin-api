package com.storycraft.server.world;

import com.google.gson.JsonObject;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.WorldServer;

public interface IWorldAddon {

    AddonHandler createHandler(StoryPlugin plugin, World world, JsonConfigEntry config);

    public class AddonHandler implements Listener {

        private JsonConfigEntry config;
        private StoryPlugin plugin;
        private IWorldAddon addon;
        private World world;

        protected AddonHandler(StoryPlugin plugin, IWorldAddon addon, World world, JsonConfigEntry config) {
            this.plugin = plugin;
            this.addon = addon;
            this.world = world;
            this.config = config;
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
        
        public JsonConfigEntry getConfig() {
            return config;
        }

        protected WorldServer getNMSWorld() {
            return getCraftWorld().getHandle();
        }

        public boolean isTargetWorld(World w) {
            return getWorld().equals(w);
        }
    }
}