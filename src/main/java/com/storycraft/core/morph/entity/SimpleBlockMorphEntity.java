package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;
import com.storycraft.server.entity.metadata.NoGravityDataWatcher;
import com.storycraft.util.BlockIdUtil;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityFallingBlock;

public class SimpleBlockMorphEntity implements IMorphEntity {

    private EntityFallingBlock entity;
    private DataWatcher metadata;

    public SimpleBlockMorphEntity(org.bukkit.entity.Entity entity, BlockData data) {
        this.entity = new EntityFallingBlock(((CraftWorld)entity.getWorld()).getHandle(), 0, 0, 0, BlockIdUtil.getNMSBlockData(data));

        this.entity.setNoGravity(true);

        this.metadata = new NoGravityDataWatcher(new ComparingDataWatcher(((CraftEntity)entity).getHandle(), this.entity));
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