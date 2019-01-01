package com.storycraft.core.morph;

import com.storycraft.core.morph.entity.SimpleMorphEntity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SimpleMorphInfo extends MorphInfo {

    public SimpleMorphInfo(Entity entity, EntityType type) {
        super(entity, new SimpleMorphEntity(entity, type));
    }

}