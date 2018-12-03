package com.storycraft.server.world;

import org.bukkit.World;
import org.bukkit.WorldType;

public interface IUniverse {

    public String getName();

    public boolean isLoaded();

    public boolean canSave();

    public long getSeed();

    public boolean isStructureGen();

    public void onLoad();
    public void onUnload();

    public World getBukkitWorld();

    public World.Environment getEnvironment();

    public WorldType getWorldType();

}