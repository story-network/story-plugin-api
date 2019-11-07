package com.storycraft.server.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.storycraft.MainPlugin;
import com.storycraft.server.registry.IRegistry;
import com.storycraft.server.registry.RegistryManager;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.DataConverterRegistry;
import net.minecraft.server.v1_14_R1.DataConverterTypes;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.RegistryMaterials;
import net.minecraft.server.v1_14_R1.SharedConstants;
import net.minecraft.server.v1_14_R1.World;

public class ServerEntityRegistry implements IRegistry<CustomEntityInfo<? extends Entity>> {

    private Reflect.WrappedField<EntityTypes.b, EntityTypes> constructorField;

    private Reflect.WrappedField<BiMap, RegistryMaterials> keyMapField;

    private Map<String, CustomEntityInfo> customEntityMap;
    private Map<Integer, CustomEntityInfo> customEntityIdMap;
    private Map<Integer, CustomEntityInfo> customEntityNetworkIdMap;

    private Map<String, EntityTypes.b> vanillaBackupMap;

    private CustomEntityConverter converter;
    private RegistryManager manager;

    public ServerEntityRegistry(RegistryManager manager){
        this.customEntityMap = new HashMap<>();
        this.customEntityIdMap = new HashMap<>();
        this.customEntityNetworkIdMap = new HashMap<>();
        this.vanillaBackupMap = new HashMap<>();

        this.converter = new CustomEntityConverter(this);

        this.manager = manager;
    }

    @Override
    public void preInitialize(MainPlugin plugin) {
        this.constructorField = Reflect.getField(EntityTypes.class, "aZ");
        this.keyMapField = Reflect.getField(RegistryMaterials.class, "c");
        constructorField.unlockFinal();
    }

    @Override
    public void initialize(MainPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(converter, plugin);
    }

    @Override
    public void unInitialize(MainPlugin plugin) {
        for (int id : customEntityIdMap.keySet()) {
            CustomEntityInfo info = customEntityIdMap.get(id);
            String name = info.getName().toString();

            removeInternal(id, info.getName());
        }

        customEntityMap.clear();
        customEntityIdMap.clear();
        customEntityNetworkIdMap.clear();
        vanillaBackupMap.clear();
    }

    @Override
    public void add(int id, CustomEntityInfo<? extends Entity> item) throws Exception {
        if (contains(item.getName().getKey()) || containsId(id))
            throw new Exception("Entity with " + item.getName() + " already exists");

        Map<String, Type<?>> types = getEntityFixerMap();
        types.put(item.getName().toString(), types.get(item.getClientEntityTypes()));

        EntityTypes.a a = EntityTypes.a.a((EntityTypes entitytypes, World world) -> { return item.getEntityConstructor().apply(entitytypes, world); }, item.getCreatureType());
        EntityTypes entityTypes = net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.a(id, item.getName(), a.a(item.getName().toString()));

        customEntityMap.put(item.getName().getKey(), item);
        customEntityIdMap.put(id, item);
        customEntityNetworkIdMap.put(net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.a(entityTypes), item);
    }

    protected Map<String, Type<?>> getEntityFixerMap() {
        return (Map<String, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
    }

    public void remove(int id) throws Exception {
        if (!containsId(id))
            throw new Exception("Custom Entity id " + id + " does not exist");

        CustomEntityInfo info = customEntityIdMap.get(id);
        MinecraftKey key = info.getName();
        removeInternal(id, key);

        int networkId = net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.a(net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.fromId(id));

        customEntityIdMap.remove(id);
        customEntityMap.remove(key.toString());
        customEntityNetworkIdMap.remove(networkId);
    }

    protected void removeInternal(int id, MinecraftKey key) {
        String name = key.toString();

        if (vanillaBackupMap.containsKey(name)) {
            EntityTypes type = EntityTypes.a(name).get();
            constructorField.set(type, vanillaBackupMap.get(name));
        } else {
            keyMapField.get(net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE).remove(key);
        }
    }

    public <T extends Entity>void addDefaultOverride(EntityTypes<? extends Entity> defaultType, CustomEntityInfo<T> item) throws Exception {
        MinecraftKey key = EntityTypes.getName(defaultType);
        if (key != null) {
            String name = key.getKey();

            int id = net.minecraft.server.v1_14_R1.IRegistry.ENTITY_TYPE.a(defaultType);

            EntityTypes type = EntityTypes.a(key.toString()).get();

            if (!vanillaBackupMap.containsKey(name))
                vanillaBackupMap.put(name, constructorField.get(type));

            constructorField.set(type, (EntityTypes entitytypes, World world) -> { return item.getEntityConstructor().apply(entitytypes, world); });

            customEntityIdMap.put(id, item);
            customEntityNetworkIdMap.put(id, item);

            customEntityMap.put(key.toString(), item);
        }
    }

    @Override
    public boolean contains(String name) {
        return customEntityMap.containsKey(name);
    }

    public boolean containsItem(CustomEntityInfo item) {
        return customEntityMap.containsValue(item);
    }

    public boolean containsNetworkId(int networkId) {
        return customEntityNetworkIdMap.containsKey(networkId);
    }

    @Override
    public int getId(CustomEntityInfo item) {
        if (!containsItem(item))
            return -1;

        for (int id : customEntityIdMap.keySet()) {
            CustomEntityInfo info = customEntityMap.get(id);
            if (info.equals(item))
                return id;
        }
        
        return -1;
    }

    @Override
    public String getName(CustomEntityInfo item) {
        if (!containsItem(item))
            return null;

        for (String name : customEntityMap.keySet()) {
            CustomEntityInfo info = customEntityMap.get(name);
            if (info.equals(item))
                return name;
        }

        return null;
    }

    @Override
    public CustomEntityInfo getByName(String name) {
        if (!customEntityMap.containsKey(name))
            return null;

        return customEntityMap.get(name);
    }

    @Override
    public CustomEntityInfo getById(int id) {
        return customEntityIdMap.get(id);
    }

    public CustomEntityInfo getByNetworkId(int id) {
        return customEntityNetworkIdMap.get(id);
    }

    public RegistryManager getRegistryManager() {
        return manager;
    }

    @Override
    public boolean containsId(int id) {
        return customEntityIdMap.containsKey(id);
    }
}
