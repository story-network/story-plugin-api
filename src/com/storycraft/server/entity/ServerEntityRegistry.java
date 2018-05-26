package com.storycraft.server.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.server.registry.IRegistry;
import com.storycraft.server.registry.RegistryManager;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.EntityTypes;

import java.util.HashMap;
import java.util.Map;

public class ServerEntityRegistry implements IRegistry<Class<? extends CustomEntity>> {

    private Map<Integer, Class<? extends CustomEntity>> entityIdMap;
    private Map<String, Class<? extends CustomEntity>> entityNameMap;

    private RegistryManager manager;
    private ClientEntityHandler handler;

    public ServerEntityRegistry(RegistryManager manager){
        this.entityIdMap = new HashMap<>();
        this.entityNameMap = new HashMap<>();

        this.manager = manager;
        this.handler = new ClientEntityHandler();
    }

    protected void addCustomEntity(int id, String keyName, Class<? extends CustomEntity> entityClass, String entityName){
        if (contains(entityClass))
            return;

        Reflect.invokeMethod(EntityTypes.class, "a", id, keyName, entityClass, entityName);

        entityIdMap.put(id, entityClass);
        entityNameMap.put(keyName, entityClass);
    }

    @Override
    public void initialize(StoryPlugin plugin) {
        handler.initialize(plugin);
    }

    @Override
    public boolean contains(Class<? extends CustomEntity> object) {
        return entityIdMap.containsValue(object) || entityNameMap.containsValue(object);
    }

    public ClientEntityHandler getHandler() {
        return handler;
    }

    @Override
    public Class<? extends CustomEntity> getByName(String name) {
        return entityNameMap.get(name);
    }

    @Override
    public Class<? extends CustomEntity> getById(int id) {
        return entityIdMap.get(id);
    }
}
