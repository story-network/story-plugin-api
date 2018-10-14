package com.storycraft.server.forge;

import com.storycraft.server.ServerExtension;
import com.storycraft.server.ServerManager;

public class ForgeServerManager extends ServerExtension {

    private ServerManager serverManager;

    private ForgeServerListPing forgeServerListPing;

    public ForgeServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        this.forgeServerListPing = null;
    }

    @Override
    public void onEnable() {
        this.forgeServerListPing = new ForgeServerListPing(this);
    }

    public ServerManager getServerManager() {
        return serverManager;
    }
}
