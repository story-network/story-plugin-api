package com.storycraft.util;

import net.minecraft.server.v1_14_R1.Block;
import net.minecraft.server.v1_14_R1.IBlockData;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.block.data.CraftBlockData;

public class BlockIdUtil {
    public static int getCombinedId(BlockData blockData) {
        return Block.getCombinedId(getNMSBlockData(blockData));
    }

    public static IBlockData getNMSBlockData(BlockData blockData) {
        return ((CraftBlockData) blockData).getState();
    }
}
