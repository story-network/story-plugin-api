package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.config.ConfigManager;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.core.combat.FastCombat;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.itemdrop.DropCounter;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.server.ServerManager;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.storage.TempStorage;
import com.storycraft.test.TestFunction;
import com.storycraft.util.Reflect;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StoryPlugin extends JavaPlugin {

    private static String TEMP_FILE_NAME = "StoryServer.jar";

    private PluginDataStorage pluginDataStorage;
    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;
    private ConfigManager localConfigManager;
    private ServerManager serverManager;

    private boolean initalized = false;

    private TempStorage tempStorage;

    public StoryPlugin() {
        this.tempStorage = new TempStorage();
        this.initalized = false;
    }

    public void init(){
        if (this.initalized)
            return;
        this.initalized = true;

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
        getMiniPluginLoader().addMiniPlugin(new DropCounter());
        getMiniPluginLoader().addMiniPlugin(new ServerMotd());
    }

    @Override
    public void onEnable() {
        if (!isInitalized()) {
            try {
                File pluginRef = new File(URLDecoder.decode(StoryPlugin.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
                getLogger().log(Level.INFO, "임시 폴더에 복사중... " + getTempStorage().getPath());
                getTempStorage().saveSync(Files.readAllBytes(pluginRef.toPath()), TEMP_FILE_NAME);

                getServer().getPluginManager().disablePlugin(this);

                Plugin plugin = getServer().getPluginManager().loadPlugin(getTempStorage().getPath().resolve(TEMP_FILE_NAME).toFile());
                Reflect.invokeMethod(plugin, "init");
                getServer().getPluginManager().enablePlugin(plugin);

                Reflect.<List<Plugin>>getField(getServer().getPluginManager(), "plugins").remove(this);
                Reflect.<Map<String, Plugin>>getField(getServer().getPluginManager(), "lookupNames").remove(getName());

                ClassLoader cl = getClass().getClassLoader();
                Reflect.setField(cl, "plugin", null);
                Reflect.setField(cl, "pluginInit", null);

                System.gc();
            } catch (Exception e) {
                getLogger().warning("플러그인 임시 복사를 실패 했습니다. " + e.getLocalizedMessage());
                init();
            }
        }
        else{
            getMiniPluginLoader().onEnable();

            TestFunction.test(this, getServer().getWorld("world"));
        }
    }

    @Override
    public void onDisable() {
        if (isInitalized()) {
            getMiniPluginLoader().onDisable(false);
        }
    }

    public boolean isInitalized() {
        return initalized;
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
        return ChatColor.GREEN + "Story Network";
    }

    public static void main(String[] args){
        System.out.println("이 프로그램은 단독 실행 될수 없습니다");
    }
}
