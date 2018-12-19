package com.storycraft.server.entity;

import java.util.function.Function;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.World;

public class CustomEntityInfo {

    private Class<? extends Entity> entityClass;
    private Function<? super World, ? extends Entity> entityConstructor;
    private EntityTypes<?> clientEntityTypes;

    private MinecraftKey name;

    public CustomEntityInfo(String name, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this(new MinecraftKey(name), entityClass, entityConstructor, clientEntityTypes);
    }

    public CustomEntityInfo(MinecraftKey name, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this.name = name;
        this.entityClass = entityClass;
        this.entityConstructor = entityConstructor;
        this.clientEntityTypes = clientEntityTypes;
    }

    public MinecraftKey getName() {
        return name;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public Function<? super World, ? extends Entity> getEntityConstructor() {
        return entityConstructor;
    }

    public EntityTypes getClientEntityTypes() {
        return clientEntityTypes;
    }

}