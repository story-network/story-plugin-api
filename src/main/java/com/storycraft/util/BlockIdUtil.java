package com.storycraft.util;

import net.minecraft.server.v1_13_R2.Block;
import net.minecraft.server.v1_13_R2.IBlockData;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData;

public class BlockIdUtil {
    public static int getCombinedId(BlockData blockData) {
        return Block.getCombinedId(getNMSBlockData(blockData));
    }

    public static IBlockData getNMSBlockData(BlockData blockData) {
        return ((CraftBlockData) blockData).getState();
    }
}
