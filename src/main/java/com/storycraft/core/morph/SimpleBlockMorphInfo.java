package com.storycraft.core.morph;

import com.storycraft.core.morph.entity.SimpleBlockMorphEntity;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

public class SimpleBlockMorphInfo extends MorphInfo {

    public SimpleBlockMorphInfo(Entity entity, BlockData data) {
        super(entity, new SimpleBlockMorphEntity(entity, data));
    }

}