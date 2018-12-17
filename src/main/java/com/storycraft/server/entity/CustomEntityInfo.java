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

    private MinecraftKey saveName;

    public CustomEntityInfo(String saveName, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this(new MinecraftKey(saveName), entityClass, entityConstructor, clientEntityTypes);
    }

    public CustomEntityInfo(MinecraftKey saveName, Class<? extends Entity> entityClass, Function<? super World, ? extends Entity> entityConstructor, EntityTypes clientEntityTypes) {
        this.saveName = saveName;
        this.entityClass = entityClass;
        this.entityConstructor = entityConstructor;
        this.clientEntityTypes = clientEntityTypes;
    }

    public MinecraftKey getSaveName() {
        return saveName;
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