package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.core.MiniPluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class StoryPlugin extends JavaPlugin {

    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;

    public StoryPlugin(){
        this.miniPluginLoader = new MiniPluginLoader(this);
        this.commandManager = new CommandManager(this);

        this.initMiniPlugin();
    }

    private void initMiniPlugin(){

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MiniPluginLoader getMiniPluginLoader() {
        return miniPluginLoader;
    }
}
