package com.storycraft.server.entity.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.storycraft.util.IBindable;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;

public class PatchedDataWatcher extends DataWatcher {

    private static Reflect.WrappedField<DataWatcher, Entity> datawatcherField;
    private static Reflect.WrappedField<Entity, DataWatcher> entityField;
    private static Reflect.WrappedField<Boolean, DataWatcher> dirtyFlag;
    private static Reflect.WrappedField<Map<Integer, Item<?>>, DataWatcher> originalListField;

    private static Reflect.WrappedMethod<Item<?>, DataWatcher> getOriginalItemMethod;

    static {
        entityField = Reflect.getField(DataWatcher.class, "entity");
        dirtyFlag = Reflect.getField(DataWatcher.class, "g");

        originalListField = Reflect.getField(DataWatcher.class, "d");

        datawatcherField = Reflect.getField(Entity.class, "datawatcher");
            
        datawatcherField.unlockFinal();
    }

    private Map<Integer, Supplier> patchMap;

    private DataWatcher original;

    public PatchedDataWatcher(DataWatcher original) {
        super(entityField.get(original));

        this.original = original;
        this.patchMap = new ConcurrentHashMap<>();
    }

    public DataWatcher getOriginal() {
        return original;
    }

    public void bindToEntity() {
        datawatcherField.set(entityField.get(this), this);
    }

    public <T> void register(DataWatcherObject<T> datawatcherobject, T t0) {
        getOriginal().register(datawatcherobject, t0);
    }
  
    public <T> T get(DataWatcherObject<T> datawatcherobject) {
        return getOriginal().get(datawatcherobject);
    }
  
    public <T> void set(DataWatcherObject<T> datawatcherobject, T t0) {
        getOriginal().set(datawatcherobject, t0);
    }
  
    public <T> void markDirty(DataWatcherObject<T> datawatcherobject) {
        getOriginal().markDirty(datawatcherobject);
    }
  
    public boolean a() {
        return getOriginal().a();
    }

    public boolean containsPatch(Integer id) {
        return patchMap.containsKey(id);
    }
  
    public <T>void addPatch(Integer id, T value) {
        addPatch(id, new Supplier<T>() {
            @Override
            public T get() {
                return value;
            }
        });
    }

    public <T>void addPatch(Integer id, Supplier<T> value) {
        patchMap.remove(id);

        patchMap.put(id, value);

        markPatchDirty(id);
    }

    public <T>void removePatch(Integer id) {
        markPatchDirty(id);

        patchMap.remove(id);
    }

    public void markPatchDirty(int id) {
        if (!patchMap.containsKey(id))
            return;
        
        Item<?> originalItem = originalListField.get(getOriginal()).get(id);

        if (originalItem != null)
            originalItem.a(true);

        dirtyFlag.set(getOriginal(), true);
    }
    
    @Override
    public List<Item<?>> b() {
        List<Item<?>> list = getOriginal().b();

        if (list == null) {
            return list;
        }

        for (int i = 0; i < list.size(); i++) {
            Item<?> originalItem = list.get(i);
            DataWatcherObject originalObj = originalItem.a();

            if (patchMap.containsKey(originalObj.a())) {
                Item<?> patchedItem = new Item<>(originalObj, patchMap.get(originalObj.a()).get());
                list.set(i, patchedItem);
            }
        }

        return list;
    }
  
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        DataWatcher.a(this.c(), packetdataserializer);
    }
  
    @Override
    public List<Item<?>> c() {
        List<Item<?>> list = getOriginal().c();

        for (int i = 0; i < list.size(); i++) {
            Item<?> originalItem = list.get(i);
            DataWatcherObject originalObj = originalItem.a();

            if (patchMap.containsKey(originalObj.a())) {
                Item<?> patchedItem = new Item<>(originalObj, patchMap.get(originalObj.a()).get());
                list.set(i, patchedItem);
            }
        }

        return list;
    }

    public boolean d() {
        return getOriginal().d();
    }
  
    public void e() {
        getOriginal().e();
    }

}