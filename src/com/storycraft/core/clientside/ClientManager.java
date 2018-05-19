package com.storycraft.core.clientside;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.MiniPluginLoader;

public class ClientManager extends MiniPlugin {

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
