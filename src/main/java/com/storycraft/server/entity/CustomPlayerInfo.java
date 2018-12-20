package com.storycraft.server.entity;

import java.util.function.Function;

import com.storycraft.server.entity.override.IPlayerOverrideProfileHandler;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.World;

public class CustomPlayerInfo extends CustomEntityInfo {

    private IPlayerOverrideProfileHandler profileHandler;

    public CustomPlayerInfo(String name, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, IPlayerOverrideProfileHandler profileHandler) {
        this(new MinecraftKey(CustomEntityInfo.DEFAULT_DOMAIN, name), entityClass, entityConstructor, profileHandler);
    }

    public CustomPlayerInfo(MinecraftKey name, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, IPlayerOverrideProfileHandler profileHandler) {
        super(name, entityClass, entityConstructor, EntityTypes.PLAYER);

        this.profileHandler = profileHandler;
    }

    public IPlayerOverrideProfileHandler getProfileHandler() {
        return profileHandler;
    }

}