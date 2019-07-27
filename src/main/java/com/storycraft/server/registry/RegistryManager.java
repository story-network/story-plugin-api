package com.storycraft.server.registry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.storycraft.MainPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.entity.ServerEntityRegistry;

public class RegistryManager extends ServerExtension {

    private ServerEntityRegistry entityRegistry;

    public RegistryManager(){
        this.entityRegistry = new ServerEntityRegistry(this);
    }

    @Override
    public void onLoad(MainPlugin plugin){
        preInitRegistry(plugin);
    }

    @Override
    public void onEnable(){
        initRegistry(getPlugin());
    }

    @Override
    public void onDisable(boolean restart){
        unInitRegistry(getPlugin());
    }

    private void preInitRegistry(MainPlugin plugin){
        getEntityRegistry().preInitialize(plugin);
    }

    private void initRegistry(MainPlugin plugin){
        getEntityRegistry().initialize(plugin);
    }

    private void unInitRegistry(MainPlugin plugin){
        getEntityRegistry().unInitialize(plugin);
    }

    public ServerEntityRegistry getEntityRegistry() {
        return entityRegistry;
    }
}
