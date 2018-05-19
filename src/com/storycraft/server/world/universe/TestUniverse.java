package com.storycraft.server.world.universe;

import com.storycraft.server.world.CustomUniverse;
import org.bukkit.World;
import org.bukkit.WorldType;

public class TestUniverse extends CustomUniverse {
    public TestUniverse(String name) {
        super(name);
    }

    public TestUniverse(String name, long seed){
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
}
