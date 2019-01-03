package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.IEntityHandler;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public interface IMorphEntity extends IEntityHandler {

    public Entity getNMSEntity();
    
    public DataWatcher getFixedMetadata();
}