package com.storycraft.config;

import com.storycraft.StoryPlugin;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.util.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class ConfigManager {

    private Map<String, IConfigFile> configFileMap;

    private ConfigHandler configHandler;
    private StoryPlugin plugin;

    public ConfigManager(StoryPlugin plugin) {
        this.plugin = plugin;

        this.configFileMap = new HashMap<>();
        this.configHandler = new ConfigHandler(this);

        plugin.getMiniPluginLoader().addMiniPlugin(getConfigHandler());
    }

    public AsyncTask<Void> addConfigFile(String name, IConfigFile configFile) {
        return new AsyncTask<Void>(new AsyncTask.AsyncCallable<Void>() {
            @Override
            public Void get() {
                if (hasConfigFile(name))
                    return null;

                try {
                    configFile.load(new ByteArrayInputStream(getDataStorage().getSync(name)));
                    getConfigFileMap().put(name, configFile);
                } catch (IOException e) {
                    getPlugin().getLogger().warning(name + " 을 로드 중 오류가 발생했습니다. " + e.getLocalizedMessage());
                }

                return null;
            }
        });
    }

    public PluginDataStorage getDataStorage(){
        return getPlugin().getDataStorage();
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

    public StoryPlugin getPlugin() {
        return plugin;
    }

    protected ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
