package com.storycraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.storycraft.MainPlugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager {

    public static String UNKNOWN_COMMAND = "알 수 없는 커맨드 입니다";

    private MainPlugin plugin;
    private CommandListener listener;

    private CommandDispatcher<CommandSender> engine;

    private Map<String[], ICommand> commandMap = new HashMap<>();

    public CommandManager(MainPlugin plugin){
        this.plugin = plugin;
        this.listener = new CommandListener(this);
        this.engine = new CommandDispatcher<>();

        plugin.getMiniPluginLoader().addMiniPlugin(listener);
    }

    public ICommand getCommand(String str){
        for (String[] aliases : getCommandMap().keySet()){
            for (String alias : aliases){
                if (str.equals(alias))
                    return getCommandMap().get(aliases);
            }
        }

        return null;
    }

    public void addCommand(ICommand command){
        getCommandMap().put(command.getAliases(), command);
    }

    public MainPlugin getPlugin() {
        return plugin;
    }

    protected CommandDispatcher<CommandSender> getEngine() {
        return engine;
    }

    protected Map<String[], ICommand> getCommandMap() {
        return commandMap;
    }
}
