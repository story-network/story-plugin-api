package com.storycraft.server.world;

import org.dozer.DozerBeanMapper;

import net.minecraft.server.v1_13_R2.WorldServer;

public class PatchedWorldServer extends WorldServer {

    private WorldServer original;
    private boolean initialized;

    public PatchedWorldServer() {
        super(null, null, null, null, null, null, null, null);
        initialized = false;
    }

    public void initialize(WorldServer server) {
        original = server;
        initialized = true;
    }

    public WorldServer getOriginal() {
        return original;
    }

    public boolean IsInitialized() {
        return initialized;
    }

    public void updateFromOriginal() {
        DozerBeanMapper mapper = new DozerBeanMapper();

        mapper.map(original, this);
    }

    public void updateOriginal() {
        DozerBeanMapper mapper = new DozerBeanMapper();

        mapper.map(this, original);
    }


}