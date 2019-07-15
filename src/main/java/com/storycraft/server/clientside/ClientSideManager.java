package com.storycraft.server.clientside;

import com.storycraft.StoryPlugin;
import com.storycraft.MiniPluginLoader;
import com.storycraft.server.ServerExtension;

public class ClientSideManager extends ServerExtension {

    private ClientEntityManager clientEntityManager;
    private ClientBlockManager clientBlockManager;

    public ClientSideManager(){
        this.clientEntityManager = new ClientEntityManager();
        this.clientBlockManager = new ClientBlockManager();
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        MiniPluginLoader loader = plugin.getMiniPluginLoader();
        loader.addMiniPlugin(getClientEntityManager());
        loader.addMiniPlugin(getClientBlockManager());
    }

    public ClientEntityManager getClientEntityManager() {
        return clientEntityManager;
    }

    public ClientBlockManager getClientBlockManager() {
        return clientBlockManager;
    }
}
