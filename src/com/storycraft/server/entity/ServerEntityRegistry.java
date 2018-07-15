package com.storycraft.server.entity;

import com.storycraft.StoryPlugin;
import com.storycraft.server.registry.IRegistry;
import com.storycraft.server.registry.RegistryManager;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_12_R1.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerEntityRegistry implements IRegistry<Class<? extends Entity>> {

    private Map<Integer, Class<? extends Entity>> entityIdMap;
    private Map<String, Class<? extends Entity>> entityNameMap;

    private RegistryManager manager;
    private ClientEntityHandler handler;

    private Reflect.WrappedField<List<String>, EntityTypes> nameListField;

    public ServerEntityRegistry(RegistryManager manager){
        this.entityIdMap = new HashMap<>();
        this.entityNameMap = new HashMap<>();

        this.manager = manager;
        this.handler = new ClientEntityHandler();

        this.nameListField = Reflect.getField(EntityTypes.class, "g");
    }

    protected void addCustomEntity(int id, String saveName, Class<? extends Entity> entityClass, Class<? extends Entity> clientEntityClass, String entityName){
        if (contains(entityClass))
            return;

        int clientEntityId = EntityTypes.b.a(clientEntityClass);

        //from nms code start
        MinecraftKey saveKey = new MinecraftKey(saveName);
        EntityTypes.b.a(clientEntityId, saveKey, entityClass);
        EntityTypes.d.add(saveKey);

        List<String> nameList = nameListField.get(null);

        while(nameList.size() <= id) {
            nameList.add(null);
        }

        nameList.set(id, entityName);
        //from nms code end

        entityIdMap.put(id, entityClass);
        entityNameMap.put(saveName, entityClass);
    }

    @Override
    public void preInitialize(StoryPlugin plugin) {

    }

    @Override
    public void initialize(StoryPlugin plugin) {
        handler.initialize(plugin);
    }

    @Override
    public boolean contains(Class<? extends Entity> object) {
        return entityIdMap.containsValue(object) || entityNameMap.containsValue(object);
    }

    public ClientEntityHandler getHandler() {
        return handler;
    }

    @Override
    public Class<? extends Entity> getByName(String name) {
        return entityNameMap.get(name);
    }

    @Override
    public Class<? extends Entity> getById(int id) {
        return entityIdMap.get(id);
    }
}
