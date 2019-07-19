package com.storycraft.server.morph;

import com.storycraft.server.morph.entity.SimpleBlockMorphEntity;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

public class SimpleBlockMorphInfo extends MorphInfo {

    public SimpleBlockMorphInfo(Entity entity, BlockData data) {
        super(entity, new SimpleBlockMorphEntity(entity, data));
    }

}