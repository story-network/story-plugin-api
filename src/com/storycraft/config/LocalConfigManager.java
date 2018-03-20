package com.storycraft.config;

import com.storycraft.StoryPlugin;

import java.util.HashMap;
import java.util.Map;

public class LocalConfigManager {
    private Map<String, IConfigFile> configFileMap;
    private ConfigHandler configHandler;

    public LocalConfigManager(StoryPlugin plugin) {
        this.configFileMap = new HashMap<>();
        this.configHandler = new ConfigHandler(this);

        plugin.getMiniPluginLoader().addMiniPlugin(getConfigHandler());
    }

    public void addConfigFile(String name, IConfigFile configFile) {
        getConfigFileMap().put(name, configFile);
    }

    public boolean hasConfigFile(String name) {
        return getConfigFileMap().containsKey(name);
    }

    public IConfigFile getConfigFile(String name) {
        return getConfigFileMap().get(name);
    }

    protected Map<String, IConfigFile> getConfigFileMap() {
        return configFileMap;
    }

    protected ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
