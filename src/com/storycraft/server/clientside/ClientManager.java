package com.storycraft.server.clientside;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.server.ServerExtension;

public class ClientManager extends ServerExtension {

    private ClientEntityManager clientEntityManager;

    public ClientManager(){
        this.clientEntityManager = new ClientEntityManager();
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();
        loader.addMiniPlugin(getClientEntityManager());
    }

    public ClientEntityManager getClientEntityManager() {
        return clientEntityManager;
    }
}
