package com.storycraft.core.morph.entity;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class MorphEntity implements IMorphEntity {

    private Entity entity;
    private DataWatcher metadata;

    public MorphEntity(Entity entity, DataWatcher metadata) {
        this.entity = entity;
        this.metadata = metadata;
    }

    @Override
    public Entity getNMSEntity() {
        return entity;
    }

    @Override
	public DataWatcher getFixedMetadata() {
		return metadata;
	}

}