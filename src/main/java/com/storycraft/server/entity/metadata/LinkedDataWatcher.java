package com.storycraft.server.entity.metadata;

import java.util.List;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.Entity;

public class LinkedDataWatcher extends DataWatcher {

    public LinkedDataWatcher(Entity entity) {
        super(entity);
    }

    @Override
    public List<Item<?>> b() {
        return null;
    }

    @Override
    public List<Item<?>> c() {
        return null;
    }



}