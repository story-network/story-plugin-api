package com.storycraft.server.chunk;

import com.storycraft.StoryPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.util.AsyncTask;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PlayerChunk;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;
import net.minecraft.server.v1_14_R1.WorldServer;
import net.minecraft.server.v1_14_R1.PlayerChunkMap.EntityTracker;

public class AsyncChunkLoader extends ServerExtension implements Listener {

    @Override
    public void onLoad(StoryPlugin plugin) {

    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    protected WorldServer getNMSWorld(World w) {
        return (WorldServer) ((CraftWorld) w).getHandle();
    }

    public boolean isChunkLoaded(World w, int x, int z) {
        return w.isChunkLoaded(x, z);
    }

    public AsyncTask<Void> loadChunkAsync(World w, int x, int z) {
        return new AsyncTask<Void>(() -> {
            if (isChunkLoaded(w, x, z))
                return null;

            WorldServer nmsWorld = getNMSWorld(w);

            return null;
        });
    }

    public AsyncTask<Void> unloadChunkAsync(World w, int x, int z, boolean save) {
        return new AsyncTask<Void>(() -> {
            if (!isChunkLoaded(w, x, z))
                return null;

            WorldServer nmsWorld = getNMSWorld(w);

            return null;
        });
    }

}