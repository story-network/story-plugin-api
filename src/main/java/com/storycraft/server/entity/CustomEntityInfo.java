package com.storycraft.server.entity;

import java.util.function.Function;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.World;

public class CustomEntityInfo {

    protected static String DEFAULT_DOMAIN = "server";

    private EnumCreatureType creatureType;
    private Function<? super World, ? extends Entity> entityConstructor;
    private EntityTypes<?> clientEntityTypes;

    private MinecraftKey name;

    public CustomEntityInfo(String name, EnumCreatureType creatureType, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this(new MinecraftKey(DEFAULT_DOMAIN, name), creatureType, entityConstructor, clientEntityTypes);
    }

    public CustomEntityInfo(MinecraftKey name, EnumCreatureType creatureType, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this.name = name;
        this.creatureType = creatureType;
        this.entityConstructor = entityConstructor;
        this.clientEntityTypes = clientEntityTypes;
    }

    public MinecraftKey getName() {
        return name;
    }

    public EnumCreatureType getCreatureType() {
        return creatureType;
    }

    public Function<? super World, ? extends Entity> getEntityConstructor() {
        return entityConstructor;
    }

    public EntityTypes getClientEntityTypes() {
        return clientEntityTypes;
    }

}