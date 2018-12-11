package com.storycraft.config;

import com.storycraft.StoryPlugin;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.Parallel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            public Void get() throws Throwable {
                if (hasConfigFile(name))
                    return null;

                getConfigFileMap().put(name, configFile);
                await(reloadConfig(name));

                return null;
            }
        });
    }

    public AsyncTask<Void> reloadConfig(String name) {
        return new AsyncTask<Void>(new AsyncTask.AsyncCallable<Void>() {
            @Override
            public Void get() {
                try {
                    IConfigFile configFile = getConfigFile(name);
                    configFile.load(new ByteArrayInputStream(getDataStorage().getSync(name)));
                } catch (Exception e) {
                    getPlugin().getLogger().warning(name + " 로드 중 오류가 발생했습니다. " + e.getLocalizedMessage());
                }

                return null;
            }
        });
    }

    public void saveAll() {
        Parallel.forEach(getConfigFileMap().keySet(), this::saveConfig);
    }

    public void saveConfig(String name) {
        if (!hasConfigFile(name))
            return;

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            IConfigFile configFile = getConfigFileMap().get(name);
            configFile.save(output);

            getDataStorage().saveSync(output.toByteArray(), name);
        } catch (IOException e) {
            getPlugin().getLogger().warning(name + " 저장 중 오류가 발생 했습니다 " + e.getLocalizedMessage());
        }
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
