package com.storycraft.server.entity;

import java.util.function.Function;

import com.storycraft.server.entity.override.IPlayerOverrideProfileHandler;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.World;

public class CustomPlayerInfo extends CustomEntityInfo {

    private IPlayerOverrideProfileHandler profileHandler;

    public CustomPlayerInfo(String name, Function<? super World, ? extends Entity> entityConstructor, IPlayerOverrideProfileHandler profileHandler) {
        this(new MinecraftKey(CustomEntityInfo.DEFAULT_DOMAIN, name), entityConstructor, profileHandler);
    }

    public CustomPlayerInfo(MinecraftKey name, Function<? super World, ? extends Entity> entityConstructor, IPlayerOverrideProfileHandler profileHandler) {
        super(name, EnumCreatureType.CREATURE, entityConstructor, EntityTypes.PLAYER);

        this.profileHandler = profileHandler;
    }

    public IPlayerOverrideProfileHandler getProfileHandler() {
        return profileHandler;
    }

}