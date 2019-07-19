package com.storycraft.server.morph;

import com.storycraft.server.morph.entity.IMorphEntity;

import org.bukkit.entity.Entity;

public class MorphInfo implements IMorphInfo {

    private Entity entity;
    private IMorphEntity morph;

    public MorphInfo(Entity entity, IMorphEntity morph) {
        this.entity = entity;
        this.morph = morph;
    }

    public Entity getEntity() {
        return entity;
    }

    public IMorphEntity getMorph() {
        return morph;
    }

}