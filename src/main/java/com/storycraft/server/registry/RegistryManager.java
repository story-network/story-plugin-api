package com.storycraft.server.registry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.storycraft.StoryPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.entity.ServerEntityRegistry;

public class RegistryManager extends ServerExtension {

    private ServerEntityRegistry entityRegistry;

    public RegistryManager(){
        this.entityRegistry = new ServerEntityRegistry(this);
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        preInitRegistry(plugin);
    }

    @Override
    public void onEnable(){
        initRegistry(getPlugin());
    }

    private void preInitRegistry(StoryPlugin plugin){
        getEntityRegistry().preInitialize(plugin);
    }

    private void initRegistry(StoryPlugin plugin){
        getEntityRegistry().initialize(plugin);
}

    public ServerEntityRegistry getEntityRegistry() {
        return entityRegistry;
    }
}
