package com.storycraft.server.morph;

import com.storycraft.server.morph.entity.IMorphEntity;

import org.bukkit.entity.Entity;

public interface IMorphInfo {

    public Entity getEntity();

    public IMorphEntity getMorph();

}