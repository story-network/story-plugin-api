package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;

import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.EntityType;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class SimpleMorphEntity implements IMorphEntity {

    private Entity entity;
    private DataWatcher metadata;

    public SimpleMorphEntity(org.bukkit.entity.Entity entity, EntityType type) {
        this.entity = ((CraftWorld)entity.getWorld()).createEntity(entity.getLocation(), type.getEntityClass());
        this.metadata = new ComparingDataWatcher(((CraftEntity)entity).getHandle(), this.entity);
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