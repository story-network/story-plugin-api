package com.storycraft.server.morph.entity;

import com.storycraft.server.entity.IEntityHandler;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;

public interface IMorphEntity extends IEntityHandler {

    public Entity getNMSEntity();
    
    public DataWatcher getFixedMetadata();
}