package com.storycraft.core.morph;

import com.storycraft.core.morph.entity.NamedMorphEntity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NamedMorphInfoWrapper implements IMorphInfo {

    private IMorphInfo info;
    private NamedMorphEntity morph;

    public NamedMorphInfoWrapper(IMorphInfo info) {
        this(info, true);
    }

    public NamedMorphInfoWrapper(IMorphInfo info, boolean customNameVisible) {
        this(info, customNameVisible, info.getEntity() instanceof Player ? ((Player) info.getEntity()).getName() : info.getEntity().getCustomName());
    }

    public NamedMorphInfoWrapper(IMorphInfo info, boolean customNameVisible, String customName) {
        this.info = info;
        this.morph = new NamedMorphEntity(info.getMorph(), customNameVisible, customName);
    }

    @Override
    public Entity getEntity() {
        return info.getEntity();
    }

    @Override
    public NamedMorphEntity getMorph() {
		return morph;
	}

}