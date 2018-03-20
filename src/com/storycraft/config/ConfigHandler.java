package com.storycraft.config;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.Parallel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigHandler extends MiniPlugin {

    private LocalConfigManager manager;

    public ConfigHandler(LocalConfigManager manager) {
        this.manager = manager;
    }

    public LocalConfigManager getManager() {
        return manager;
    }

    @Override
    public void onDisable(boolean reload) {
        Parallel.forEach(getManager().getConfigFileMap().values(), new Parallel.Operation<IConfigFile>() {
            @Override
            public void run(IConfigFile configFile) {
                File file = getManager().getFileMap().get(configFile);
                try {
                    configFile.save(new FileOutputStream(file));
                } catch (IOException e) {
                    getManager().getPlugin().getLogger().warning(file.getName() + " 저장을 실패 했습니다");
                }
            }
        });
    }
}
