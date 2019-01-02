package com.storycraft.server.entity.metadata;

import java.util.List;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.Entity;

public class NoGravityDataWatcher extends DataWatcher {

    private static DataWatcherObject object;

    static {
        object = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    }

    private DataWatcher watcher;

    private boolean noGravity;

    public NoGravityDataWatcher(DataWatcher watcher) {
        this(watcher, true);
    }

    public NoGravityDataWatcher(DataWatcher watcher, boolean noGravity) {
        super(null);

        this.noGravity = noGravity;
        this.watcher = watcher;
    }

    public boolean isNoGravity() {
        return noGravity;
    }

    public void setNoGravity(boolean flag) {
        this.noGravity = flag;
    }

    @Override
    public List<Item<?>> b() {
        List<Item<?>> list = watcher.b();

        for (Item<?> item : list) {
            if (item.a().a() == this.object.a()) {
                item.a(isNoGravity());
            }
        }

        return list;
    }

    @Override
    public List<Item<?>> c() {
        List<Item<?>> list = watcher.c();

        for (Item<?> item : list) {
            if (item.a().a() == this.object.a()) {
                item.a(isNoGravity());
            }
        }

        return list;
    }

}