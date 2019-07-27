package com.storycraft.server.plugin;

import com.storycraft.MainPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.util.reflect.Reflect;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class ServerPluginManager extends ServerExtension {

    private Reflect.WrappedField<SimpleCommandMap, SimplePluginManager> commandMapField;
    private Reflect.WrappedField<Map<String, Command>, SimpleCommandMap> knownCommandsField;

    private Reflect.WrappedField<List<Plugin>, SimplePluginManager> pluginsField;
    private Reflect.WrappedField<Map<String, Plugin>, SimplePluginManager> lookupNamesField;

    private Reflect.WrappedField<JavaPlugin, Object> pluginField;
    private Reflect.WrappedField<JavaPlugin, Object> pluginInitField;

    public ServerPluginManager() {
        this.commandMapField = Reflect.getField(SimplePluginManager.class, "commandMap");
        this.knownCommandsField = Reflect.getField(SimpleCommandMap.class, "knownCommands");

        this.pluginsField = Reflect.getField(SimplePluginManager.class, "plugins");
        this.lookupNamesField = Reflect.getField(SimplePluginManager.class, "lookupNames");

        try {
            this.pluginField = Reflect.getField(Class.forName("org.bukkit.plugin.java.PluginClassLoader"), "plugin");
            this.pluginInitField = Reflect.getField(Class.forName("org.bukkit.plugin.java.PluginClassLoader"), "pluginInit");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ServerPluginManager(MainPlugin plugin) {
        this();

        this.setEnabled(true);
        this.setPlugin(plugin);
    }

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

    public Plugin getPlugin(String name) {
        return getPluginManager().getPlugin(name);
    }

    public boolean enablePlugin(Plugin plugin){
        if (isEnabled(plugin))
            return false;

        getPluginManager().enablePlugin(plugin);
        return true;
    }

    public boolean disablePlugin(Plugin plugin){
        if (!isEnabled(plugin))
            return false;

        getPluginManager().disablePlugin(plugin);
        return true;
    }

    public boolean unloadPlugin(Plugin plugin) {
        disablePlugin(plugin);

        SimplePluginManager pluginManager = (SimplePluginManager) getPluginManager();

        SimpleCommandMap commandMap = commandMapField.get(pluginManager);
        Map<String, Command> knownCommands = knownCommandsField.get(commandMap);
        List<Plugin> plugins = pluginsField.get(pluginManager);
        Map<String, Plugin> lookupNames = lookupNamesField.get(pluginManager);

        plugins.remove(plugin);
        lookupNames.remove(plugin.getName(), plugin);

        for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Command> entry = it.next();
            if (entry.getValue() instanceof PluginCommand) {
                PluginCommand c = (PluginCommand) entry.getValue();
                if (c.getPlugin() == plugin) {
                    c.unregister(commandMap);
                    it.remove();
                }
            }
        }

        ClassLoader classLoader = plugin.getClass().getClassLoader();

        if (classLoader instanceof URLClassLoader) {
            pluginField.set(classLoader, null);
            pluginInitField.set(classLoader, null);

            try {
                ((URLClassLoader) classLoader).close();
            } catch (IOException e) {
                getPlugin().getLogger().warning("플러그인 언로드가 실패 했습니다. " + e.getLocalizedMessage());
            }
        }

        System.gc();

        return true;
    }

    public boolean isEnabled(Plugin plugin){
        return getPluginManager().isPluginEnabled(plugin);
    }
}
