package com.storycraft.core.player;

import com.storycraft.StoryPlugin;
import com.storycraft.MiniPlugin;
import com.storycraft.MiniPluginLoader;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.core.player.debug.UserDebug;
import com.storycraft.core.player.head.CustomHat;
import com.storycraft.core.player.home.HomeManager;
import com.storycraft.core.player.login.ServerKickMessage;
import com.storycraft.core.player.movement.CrawlToggler;
import com.storycraft.core.skin.PlayerCustomSkin;
import com.storycraft.core.spawn.PlayerSpawnManager;

public class PlayerManager extends MiniPlugin {

    private HomeManager homeManager;
    private PlayerSpawnManager playerSpawnManager;

    @Override
    public void onLoad(StoryPlugin plugin) {
        MiniPluginLoader loader = plugin.getMiniPluginLoader();

        loader.addMiniPlugin(new PlayerCustomSkin());
        loader.addMiniPlugin(new ServerMotd());
        loader.addMiniPlugin(homeManager = new HomeManager());
        //loader.addMiniPlugin(new CrawlToggler());
        loader.addMiniPlugin(new UserDebug());
        loader.addMiniPlugin(new CustomHat());
        loader.addMiniPlugin(new ServerKickMessage());
        loader.addMiniPlugin(playerSpawnManager = new PlayerSpawnManager());
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public PlayerSpawnManager getPlayerSpawnManager() {
        return playerSpawnManager;
    }

}