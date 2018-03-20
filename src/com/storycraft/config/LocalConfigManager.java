package com.storycraft.config;

import com.storycraft.StoryPlugin;
import com.storycraft.util.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class LocalConfigManager {
    private Map<String, IConfigFile> configFileMap;
    private Map<IConfigFile, File> fileMap;
    private ConfigHandler configHandler;
    private StoryPlugin plugin;

    public LocalConfigManager(StoryPlugin plugin) {
        this.plugin = plugin;
        this.configFileMap = new HashMap<>();
        this.configHandler = new ConfigHandler(this);

        plugin.getMiniPluginLoader().addMiniPlugin(getConfigHandler());
    }

    public File getLocalDirectory(){
        return plugin.getDataFolder();
    }

    public Future addConfigFile(String name, IConfigFile configFile) {
        getConfigFileMap().put(name, configFile);

        return new AsyncTask<>(new AsyncTask.AsyncCallable<Void>() {
            @Override
            public Void get() {
                File file = new File(getLocalDirectory(), name);

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    getFileMap().put(configFile, file);

                    configFile.load(new FileInputStream(file));
                } catch (IOException e) {
                    plugin.getLogger().warning(name + " 불러오기가 실패 했습니다 :( " + e.getLocalizedMessage());
                }

                return null;
            }
        }).run();
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

    protected Map<IConfigFile, File> getFileMap() {
        return fileMap;
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    protected ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
