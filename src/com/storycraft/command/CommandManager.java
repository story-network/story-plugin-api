package com.storycraft.command;

import com.storycraft.StoryPlugin;

import java.util.*;

public class CommandManager {
    public static String COMMAND_PREFIX = "/";

    public static String UNKNOWN_COMMAND = "알 수 없는 커맨드 입니다";

    private StoryPlugin plugin;
    private CommandListener listener;

    private Map<String[], ICommand> commandMap = new HashMap<>();

    public CommandManager(StoryPlugin plugin){
        this.plugin = plugin;
        this.listener = new CommandListener(this);

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

    public StoryPlugin getPlugin() {
        return plugin;
    }

    protected Map<String[], ICommand> getCommandMap() {
        return commandMap;
    }
}
