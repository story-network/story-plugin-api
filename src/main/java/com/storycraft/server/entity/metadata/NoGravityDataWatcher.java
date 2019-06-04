package com.storycraft.server.entity.metadata;

import java.util.List;

import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;

public class NoGravityDataWatcher extends DataWatcher {

    private static DataWatcherObject<Boolean> object;

    static {
        object = (DataWatcherObject) Reflect.getField(Entity.class, "aC").get(null);
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

        if (list != null) {
            for (Item<?> item : list) {
                if (item.a().a() == object.a()) {
                    ((Item<Boolean>) item).a(isNoGravity() ? Boolean.TRUE : Boolean.FALSE);
                    break;
                }
            }
        }

        return list;
    }

    @Override
    public List<Item<?>> c() {
        List<Item<?>> list = watcher.c();

        if (list != null) {
            for (Item<?> item : list) {
                if (item.a().a() == object.a()) {
                    ((Item<Boolean>) item).a(isNoGravity() ? Boolean.TRUE : Boolean.FALSE);
                    break;
                }
            }
        }

        return list;
    }

}