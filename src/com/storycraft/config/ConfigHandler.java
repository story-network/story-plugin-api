package com.storycraft.config;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.Parallel;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        Parallel.forEach(getManager().getConfigFileMap().keySet(), new Parallel.Operation<String>() {
            @Override
            public void run(String name) {
                try {
                    ByteOutputStream output = new ByteOutputStream();

                    IConfigFile configFile = getManager().getConfigFileMap().get(name);
                    configFile.save(output);

                    manager.getDataStorage().saveSync(output.getBytes(), name);
                } catch (IOException e) {
                    getManager().getPlugin().getLogger().warning(name + " 저장 중 오류가 발생 했습니다 " + e.getLocalizedMessage());
                }
            }
        });
    }
}
