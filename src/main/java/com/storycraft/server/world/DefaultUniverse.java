package com.storycraft.server.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

public class DefaultUniverse implements IUniverse {

    private World world;

    public DefaultUniverse(World defaultWorld) {
        this.world = defaultWorld;
    }

    @Override
    public String getName() {
        return getBukkitWorld().getName();
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean canSave() {
        return getBukkitWorld().isAutoSave();
    }

    @Override
    public long getSeed() {
        return getBukkitWorld().getSeed();
    }

    @Override
    public boolean isStructureGen() {
        return getBukkitWorld().canGenerateStructures();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public World getBukkitWorld() {
        return world;
    }

    @Override
    public Environment getEnvironment() {
        return getBukkitWorld().getEnvironment();
    }

	@Override
	public WorldType getWorldType() {
		return getBukkitWorld().getWorldType();
    }
}