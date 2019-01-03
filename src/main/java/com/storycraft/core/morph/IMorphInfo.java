package com.storycraft.core.morph;

import com.storycraft.core.morph.entity.IMorphEntity;

import org.bukkit.entity.Entity;

public interface IMorphInfo {

    public Entity getEntity();

    public IMorphEntity getMorph();

}