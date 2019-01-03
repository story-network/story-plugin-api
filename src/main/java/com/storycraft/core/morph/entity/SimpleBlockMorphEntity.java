package com.storycraft.core.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;
import com.storycraft.server.entity.metadata.NoGravityDataWatcher;
import com.storycraft.util.BlockIdUtil;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.EntityFallingBlock;

public class SimpleBlockMorphEntity extends HoldedMorphEntity {

    private DataWatcher metadata;

    public SimpleBlockMorphEntity(org.bukkit.entity.Entity entity, BlockData data) {
        super(new EntityFallingBlock(((CraftWorld)entity.getWorld()).getHandle(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), BlockIdUtil.getNMSBlockData(data)));

        this.metadata = new NoGravityDataWatcher(new ComparingDataWatcher(((CraftEntity)entity).getHandle(), this.getNMSEntity()));
    }

    @Override
	public DataWatcher getFixedMetadata() {
		return metadata;
    }
}