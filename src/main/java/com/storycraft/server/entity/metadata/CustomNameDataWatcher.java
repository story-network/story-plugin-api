package com.storycraft.server.entity.metadata;

import java.util.List;
import java.util.Optional;

import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.Entity;

public class CustomNameDataWatcher extends DataWatcher {

    private static DataWatcherObject customNameVisibleObject;
    private static DataWatcherObject customNameObject;

    static {
        customNameObject = (DataWatcherObject) Reflect.getField(Entity.class, "aE").get(null);
        customNameVisibleObject = (DataWatcherObject) Reflect.getField(Entity.class, "aF").get(null);
    }

    private DataWatcher watcher;

    private boolean customNameVisible;
    private String customName;

    public CustomNameDataWatcher(DataWatcher watcher, boolean customNameVisible, String customName) {
        super(null);

        this.customNameVisible = customNameVisible;
        this.customName = customName;

        this.watcher = watcher;
    }


    public boolean isCustomNameVisible() {
        return customNameVisible;
    }
    
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setCustomNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
    }

    @Override
    public List<Item<?>> b() {
        List<Item<?>> list = watcher.b();

        for (Item<?> item : list) {
            int id = item.a().a();

            if (id == customNameVisibleObject.a()) {
                ((Item<Boolean>) item).a(isCustomNameVisible() ? Boolean.TRUE : Boolean.FALSE);
            }
            else if (id == customNameObject.a()) {
                ((Item<Object>) item).a(Optional.of(new ChatComponentText(getCustomName())));
            }
        }

        return list;
    }

    @Override
    public List<Item<?>> c() {
        List<Item<?>> list = watcher.c();

        for (Item<?> item : list) {
            int id = item.a().a();

            if (id == customNameVisibleObject.a()) {
                ((Item<Boolean>) item).a(isCustomNameVisible() ? Boolean.TRUE : Boolean.FALSE);
            }
            else if (id == customNameObject.a()) {
                ((Item<Object>) item).a(Optional.of(new ChatComponentText(getCustomName())));
            }
        }

        return list;
    }

}