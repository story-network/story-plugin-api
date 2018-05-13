package com.storycraft.server.plugin;

import com.storycraft.StoryPlugin;
import com.storycraft.server.ServerExtension;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

public class ServerPluginManager extends ServerExtension {

    public PluginManager getPluginManager(){
        return getPlugin().getServer().getPluginManager();
    }

    public Plugin loadPlugin(File file){
        try {
            return getPluginManager().loadPlugin(file);
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            getPlugin().getLogger().warning("플러그인을 로드 할 수 없습니다 " + e.getLocalizedMessage());
        }

        return null;
    }

    public void enablePlugin(Plugin plugin){
        if (isEnabled(plugin))
            return;

        getPluginManager().enablePlugin(plugin);
    }

    public void disablePlugin(Plugin plugin){
        if (!isEnabled(plugin))
            return;

        getPluginManager().disablePlugin(plugin);
    }

    public boolean isEnabled(Plugin plugin){
        return getPluginManager().isPluginEnabled(plugin);
    }
}
