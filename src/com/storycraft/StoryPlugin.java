package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.config.LocalConfigManager;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.core.combat.FastCombat;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.motd.ServerMotd;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class StoryPlugin extends JavaPlugin {

    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;
    private LocalConfigManager localConfigManager;

    public StoryPlugin() {
        this.miniPluginLoader = new MiniPluginLoader(this);
        this.localConfigManager = new LocalConfigManager(this);
        this.commandManager = new CommandManager(this);

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

    public LocalConfigManager getLocalConfigManager() {
        return localConfigManager;
    }

    public String getServerName(){
        return ChatColor.YELLOW + "Story Network";
    }
}
