package com.storycraft.config;

import com.storycraft.MainMiniPlugin;

public class ConfigHandler extends MainMiniPlugin {

    private ConfigManager manager;

    public ConfigHandler(ConfigManager manager) {
        this.manager = manager;
    }

    public ConfigManager getManager() {
        return manager;
    }

    @Override
    public void onDisable(boolean reload) {
        getManager().saveAll();
    }
}
