package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.config.ConfigManager;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.core.combat.FastCombat;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.server.ServerManager;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.storage.TempStorage;
import com.storycraft.test.TestFunction;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class StoryPlugin extends JavaPlugin {

    private PluginDataStorage pluginDataStorage;
    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;
    private ConfigManager localConfigManager;
    private ServerManager serverManager;

    private TempStorage tempStorage;

    public StoryPlugin() {
        this.tempStorage = new TempStorage();

        this.pluginDataStorage = new PluginDataStorage(this);
        this.miniPluginLoader = new MiniPluginLoader(this);
        this.localConfigManager = new ConfigManager(this);
        this.commandManager = new CommandManager(this);
        this.serverManager = new ServerManager(this);

        initMiniPlugin();
    }

    private void initMiniPlugin() {
        getMiniPluginLoader().addMiniPlugin(new Explosion());
        getMiniPluginLoader().addMiniPlugin(new EntityBlood());
        getMiniPluginLoader().addMiniPlugin(new FastCombat());
        getMiniPluginLoader().addMiniPlugin(new ServerMotd());
    }

    @Override
    public void onEnable() {
        getMiniPluginLoader().onEnable();

        TestFunction.test(this, getServer().getWorld("world"));
    }

    @Override
    public void onDisable() {
        getMiniPluginLoader().onDisable(false);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MiniPluginLoader getMiniPluginLoader() {
        return miniPluginLoader;
    }

    public PluginDataStorage getDataStorage() {
        return pluginDataStorage;
    }

    public ConfigManager getConfigManager() {
        return localConfigManager;
    }

    public TempStorage getTempStorage() {
        return tempStorage;
    }

    public String getServerName(){
        return ChatColor.YELLOW + "Story Network";
    }
}
