package com.storycraft.server.world.universe;

import javax.annotation.Nullable;

import com.storycraft.server.world.CustomUniverse;

import org.bukkit.World;
import org.bukkit.WorldType;

import net.minecraft.server.v1_13_R2.ExceptionWorldConflict;
import net.minecraft.server.v1_13_R2.IProgressUpdate;

public class TestUniverse extends CustomUniverse {
    public TestUniverse(String name) {
        super(name);
    }

    public TestUniverse(String name, long seed){
        super(name, seed);
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.NORMAL;
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
}
