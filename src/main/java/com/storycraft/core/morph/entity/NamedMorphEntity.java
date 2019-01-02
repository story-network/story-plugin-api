package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.CustomNameDataWatcher;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class NamedMorphEntity extends MorphEntity {

    public NamedMorphEntity(Entity entity, DataWatcher watcher, boolean customNameVisible, String customName) {
        super(entity, new CustomNameDataWatcher(watcher, customNameVisible, customName));
    }
}