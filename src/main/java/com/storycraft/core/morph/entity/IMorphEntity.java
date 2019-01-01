package com.storycraft.core.morph.entity;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public interface IMorphEntity {

    public Entity getNMSEntity();
    
    public DataWatcher getFixedMetadata();
}