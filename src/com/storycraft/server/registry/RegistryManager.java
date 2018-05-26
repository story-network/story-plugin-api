package com.storycraft.server.registry;

import com.storycraft.server.ServerExtension;
import com.storycraft.server.entity.ServerEntityRegistry;

public class RegistryManager extends ServerExtension {

    private ServerEntityRegistry entityRegistry;

    public RegistryManager(){
        this.entityRegistry = new ServerEntityRegistry(this);
    }

    @Override
    public void onEnable(){
        initRegistry();
    }

    private void initRegistry(){
        getEntityRegistry().initialize(getPlugin());
    }

    public ServerEntityRegistry getEntityRegistry() {
        return entityRegistry;
    }
}
