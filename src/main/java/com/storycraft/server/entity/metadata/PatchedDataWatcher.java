package com.storycraft.server.entity.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;

public class PatchedDataWatcher extends DataWatcher {

    private static Reflect.WrappedField<DataWatcher, Entity> datawatcherField;
    private static Reflect.WrappedField<Entity, DataWatcher> entityField;

    static {
        entityField = Reflect.getField(DataWatcher.class, "c");
        datawatcherField = Reflect.getField(Entity.class, "datawatcher");
            
        datawatcherField.unlockFinal();
    }

    private Map<DataWatcherObject, Item> patchMap;

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

    public boolean containsPatch(DataWatcherObject datawatcherobject) {
        return patchMap.containsKey(datawatcherobject);
    }
  
    public <T>void addPatch(DataWatcherObject<T> datawatcherobject, T value) {
        patchMap.remove(datawatcherobject);
        
        patchMap.put(datawatcherobject, new Item<T>(datawatcherobject, value));

        try {
            markDirty(datawatcherobject);
        } catch (Exception e) {
            
        }
    }

    public <T>void removePatch(DataWatcherObject<T> datawatcherobject) {
        patchMap.remove(datawatcherobject);
        try {
            markDirty(datawatcherobject);
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public List<Item<?>> b() {
        List<Item<?>> list = getOriginal().b();

        for (int i = 0; i < list.size(); i++) {
            Item<?> item = list.get(i);
            if (patchMap.containsKey(item.a())) {
                list.remove(i);
                list.add(i, patchMap.get(item.a()));
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
            Item<?> item = list.get(i);
            if (patchMap.containsKey(item.a())) {
                list.remove(i);
                list.add(i, patchMap.get(item.a()));
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