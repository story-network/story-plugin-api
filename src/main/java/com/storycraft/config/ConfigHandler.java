package com.storycraft.config;

import com.storycraft.core.MiniPlugin;

public class ConfigHandler extends MiniPlugin {

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
