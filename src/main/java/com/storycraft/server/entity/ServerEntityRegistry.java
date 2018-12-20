package com.storycraft.server.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.storycraft.StoryPlugin;
import com.storycraft.server.registry.IRegistry;
import com.storycraft.server.registry.RegistryManager;
import net.minecraft.server.v1_13_R2.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ServerEntityRegistry implements IRegistry<CustomEntityInfo> {

    private Map<String, CustomEntityInfo> customEntityMap;
    private Map<Integer, CustomEntityInfo> customEntityIdMap;
    private Map<Integer, CustomEntityInfo> customEntityNetworkIdMap;

    private CustomEntityConverter converter;
    private RegistryManager manager;

    public ServerEntityRegistry(RegistryManager manager){
        this.customEntityMap = new HashMap<>();
        this.customEntityIdMap = new HashMap<>();
        this.customEntityNetworkIdMap = new HashMap<>();

        this.converter = new CustomEntityConverter(this);

        this.manager = manager;
    }

    protected <T extends Entity>EntityTypes.a<T> createA(Class<? extends T> entityClass, Function<? super World, ? extends T> entityConstructor) {
        return EntityTypes.a.a(entityClass, entityConstructor);
    }

    @Override
    public void preInitialize(StoryPlugin plugin) {

    }

    @Override
    public void initialize(StoryPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(converter, plugin);
    }

    @Override
    public void unInitialize(StoryPlugin plugin) {
        
    }

    @Override
    public void add(int id, CustomEntityInfo item) throws Exception {
        if (contains(item.getName().getKey()) || containsId(id))
            throw new Exception("Entity with " + item.getName() + " already exists");

        EntityTypes.a a = createA(item.getEntityClass(), item.getEntityConstructor());

        //1.13.2
        Schema sch = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(1631));
        TaggedChoice.TaggedChoiceType<?> choice = sch.findChoiceType(DataConverterTypes.n);

        Map<Object, Type<?>> types = (Map<Object, Type<?>>) choice.types();

        String key = item.getName().b() + ":" + item.getName().getKey();
        Type<?> value = types.get(EntityTypes.getName(item.getClientEntityTypes()));

        if (types.containsKey(key)) {
            types.remove(key);
        }

        types.put(key, value);

        EntityTypes entityTypes = a.a(key);
        net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(id, item.getName(), entityTypes);

        customEntityMap.put(item.getName().getKey(), item);
        customEntityIdMap.put(id, item);
        customEntityNetworkIdMap.put(net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(entityTypes), item);
    }

    public void addDefaultOverride(EntityTypes defaultType, CustomEntityInfo item) throws Exception {
        MinecraftKey key = EntityTypes.getName(defaultType);
        if (key != null) {
            String name = key.getKey();
            customEntityMap.put(name, item);

            int id = net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(defaultType);

            EntityTypes.a a = createA(item.getEntityClass(), item.getEntityConstructor());
            EntityTypes entityTypes = a.a(name);

            net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(id, key, entityTypes);

            customEntityIdMap.put(id, item);
            customEntityNetworkIdMap.put(net.minecraft.server.v1_13_R2.IRegistry.ENTITY_TYPE.a(entityTypes), item);
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
