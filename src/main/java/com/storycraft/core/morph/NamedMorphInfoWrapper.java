package com.storycraft.core.morph;

import com.storycraft.core.morph.entity.NamedMorphEntity;

import org.bukkit.entity.Player;

public class NamedMorphInfoWrapper extends MorphInfo {

    public NamedMorphInfoWrapper(MorphInfo info) {
        super(info.getEntity(), new NamedMorphEntity(info.getMorph().getNMSEntity(), info.getMorph().getFixedMetadata(), true, info.getEntity() instanceof Player ? ((Player) info.getEntity()).getName() : info.getEntity().getCustomName()));
    }

}