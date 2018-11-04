package com.storycraft.server.world.universe;

import javax.annotation.Nullable;

import com.storycraft.server.world.CustomUniverse;
import com.storycraft.server.world.PatchedWorldServer;

import org.bukkit.World;
import org.bukkit.WorldType;

import net.minecraft.server.v1_13_R2.ExceptionWorldConflict;
import net.minecraft.server.v1_13_R2.IProgressUpdate;

public class TestUniverse extends CustomUniverse {
    public TestUniverse(String name) {
        super(name);
    }

    public TestUniverse(String name, long seed){
        super(name, seed);
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.FLAT;
    }

    @Override
    public World.Environment getEnvironment() {
        return World.Environment.NORMAL;
    }

    public PatchedWorldServer createWorldProxy() {
        return new PatchedWorldServer();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    public class CutomizedWorldProxy extends PatchedWorldServer {
        @Override
        public void save(boolean flag, @Nullable IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
            super.save(flag, iprogressupdate);

            System.out.print("saved!");
        }
    }
}
