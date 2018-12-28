package com.storycraft.core.player;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.core.player.debug.UserDebug;
import com.storycraft.core.player.home.HomeManager;
import com.storycraft.core.player.login.ServerKickMessage;
import com.storycraft.core.skin.PlayerCustomSkin;

public class PlayerManager extends MiniPlugin {

    private HomeManager homeManager;

    @Override
    public void onLoad(StoryPlugin plugin) {
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(new PlayerCustomSkin());
        loader.addMiniPlugin(new ServerMotd());
        loader.addMiniPlugin(homeManager = new HomeManager());
        loader.addMiniPlugin(new UserDebug());
        loader.addMiniPlugin(new ServerKickMessage());
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

}