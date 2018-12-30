package com.storycraft.server.world.universe;

import com.storycraft.server.world.CustomUniverse;

import org.bukkit.World;
import org.bukkit.WorldType;

public class BuildUniverse extends CustomUniverse {
    
    public BuildUniverse(String name) {
        super(name);
    }

    public BuildUniverse(String name, long seed){
        super(name, seed);
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.FLAT;
    }

    @Override
    public World.Environment getEnvironment() {
        return World.Environment.NORMAL;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public String[] getRequiredAddonList() {
        return new String[] { "NoPhysics", "FixedMob", "NoFallDamage" };
    }
}