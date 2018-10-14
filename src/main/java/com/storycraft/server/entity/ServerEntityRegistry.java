package com.storycraft.server.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.server.registry.IRegistry;
import com.storycraft.server.registry.RegistryManager;
import net.minecraft.server.v1_13_R1.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ServerEntityRegistry implements IRegistry<EntityTypes> {

    private Map<EntityTypes, EntityTypes> clientEntityMap;

    private CustomEntityConverter converter;
    private RegistryManager manager;

    public ServerEntityRegistry(RegistryManager manager){
        this.clientEntityMap = new HashMap<>();
        this.converter = new CustomEntityConverter(this);

        this.manager = manager;
    }

    protected <T extends Entity>void addCustomEntity(String entityName, Class<? extends T> entityClass, Function<? super World, ? extends T> entityConstructor, EntityTypes clientEntityTypes) throws Exception {
        EntityTypes old = EntityTypes.a(entityName);

        if (contains(old))
            throw new Exception("Entity with " + entityName + " already exists");

        EntityTypes.a a = createA(entityClass, entityConstructor);
        MinecraftKey saveKey = new MinecraftKey(entityName);
        EntityTypes entityTypes = EntityTypes.a(entityName, a);


        EntityTypes.REGISTRY.a(saveKey, entityTypes);

        clientEntityMap.put(entityTypes, clientEntityTypes);
    }

    public <T extends Entity>EntityTypes.a<T> createA(Class<? extends T> entityClass, Function<? super World, ? extends T> entityConstructor) {
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
    public boolean contains(EntityTypes entityTypes) {
        return clientEntityMap.containsKey(entityTypes);
    }

    @Override
    public EntityTypes getByName(String name) {
        return EntityTypes.a(name);
    }

    @Override
    public EntityTypes getById(int id) {
        return EntityTypes.REGISTRY.getId(id);
    }

    public EntityTypes getClientEntityTypes(EntityTypes entityTypes) {
        return clientEntityMap.get(entityTypes);
    }
}
