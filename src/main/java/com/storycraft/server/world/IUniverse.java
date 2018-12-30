package com.storycraft.server.world;

import org.bukkit.World;
import org.bukkit.WorldType;

public interface IUniverse {

    String getName();

    boolean isLoaded();

    boolean canSave();

    long getSeed();

    boolean isStructureGen();

    void onLoad();
    void onUnload();

    World getBukkitWorld();

    World.Environment getEnvironment();

    WorldType getWorldType();

    default String[] getRequiredAddonList() {
        return new String[]{};
    }

}