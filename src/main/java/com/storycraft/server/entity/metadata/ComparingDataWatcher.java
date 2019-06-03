package com.storycraft.server.entity.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.Entity;

public class ComparingDataWatcher extends DataWatcher {

    private static WrappedField<Boolean, DataWatcher> dirtyFlagField;
    private static WrappedField<Map<Integer, Item<?>>, DataWatcher> itemMapField;

    static {
        dirtyFlagField = Reflect.getField(DataWatcher.class, "g");
        itemMapField = Reflect.getField(DataWatcher.class, "d");
    }

    private Entity entity;
    private Entity convertTarget;

    public ComparingDataWatcher(Entity entity, Entity convertTarget) {
        super(entity);

        this.entity = entity;
        this.convertTarget = convertTarget;
    }

    public Entity getConvertTarget() {
        return convertTarget;
    }

    @Override
    public List<Item<?>> b() {
        DataWatcher watcher = this.entity.getDataWatcher();

        if (!watcher.a()) {
            dirtyFlagField.set(watcher, true);
        }

        List<Item<?>> list = this.entity.getDataWatcher().b();

        if (list == null)
            return null;

        DataWatcher targetMetadata = getConvertTarget().getDataWatcher();

        Map<Integer, Item<?>> targetMap = itemMapField.get(targetMetadata);

        for (Item<?> item : new ArrayList<>(list)) {
            next:
            for (int id : targetMap.keySet()) {
                if (item.a().a() == id) {
                    if (!item.b().getClass().isInstance(targetMap.get(id).b()))
                        list.remove(item);

                    break next;
                }
            }
        }
        
        return list;
    }

    @Override
    public List<Item<?>> c() {
        DataWatcher watcher = this.entity.getDataWatcher();

        List<Item<?>> list = this.entity.getDataWatcher().c();

        if (list == null)
            return null;

        DataWatcher targetMetadata = getConvertTarget().getDataWatcher();

        Map<Integer, Item<?>> targetMap = itemMapField.get(targetMetadata);

        for (Item<?> item : new ArrayList<>(list)) {
            next:
            for (int id : targetMap.keySet()) {
                if (item.a().a() == id) {
                    if (!item.b().getClass().isInstance(targetMap.get(id).b()))
                        list.remove(item);

                    break next;
                }
            }
        }
        
        return list;
    }
}