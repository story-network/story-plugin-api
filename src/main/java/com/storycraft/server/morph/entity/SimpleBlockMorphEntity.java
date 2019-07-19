package com.storycraft.server.morph.entity;

import com.storycraft.server.entity.metadata.ComparingDataWatcher;
import com.storycraft.server.entity.metadata.PatchedDataWatcher;
import com.storycraft.util.BlockIdUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityFallingBlock;

public class SimpleBlockMorphEntity extends HoldedMorphEntity {

    private static Reflect.WrappedField<DataWatcherObject<Boolean>, Entity> noGravityObject;

    static {
        noGravityObject = Reflect.getField(Entity.class, "aC");
    }

    private DataWatcher metadata;

    public SimpleBlockMorphEntity(org.bukkit.entity.Entity entity, BlockData data) {
        super(new EntityFallingBlock(((CraftWorld)entity.getWorld()).getHandle(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), BlockIdUtil.getNMSBlockData(data)));

        this.metadata = new PatchedDataWatcher(new ComparingDataWatcher(((CraftEntity)entity).getHandle(), this.getNMSEntity()));

        ((PatchedDataWatcher) metadata).addPatch(noGravityObject.get(null).a(), true);
    }

    @Override
	public DataWatcher getFixedMetadata() {
		return metadata;
    }
}