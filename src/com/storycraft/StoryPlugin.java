package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.config.ConfigManager;
import com.storycraft.core.MiniPluginLoader;
import com.storycraft.core.ServerDecorator;
import com.storycraft.core.combat.DamageHologram;
import com.storycraft.core.jukebox.JukeboxPlay;
import com.storycraft.core.combat.FastCombat;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.dropping.DropCounter;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.server.ServerManager;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.storage.TempStorage;
import com.storycraft.test.TestFunction;
import com.storycraft.util.reflect.Reflect;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StoryPlugin extends JavaPlugin {

    private static String TEMP_FILE_NAME = "StoryServer.jar";
    
    private File originalFile;

    private PluginDataStorage pluginDataStorage;
    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;
    private ConfigManager localConfigManager;
    private ServerManager serverManager;

    private ServerDecorator decorator;

    private boolean initalized = false;

    private TempStorage tempStorage;

    public StoryPlugin() {
        this.tempStorage = new TempStorage();
        this.initalized = false;
    }

    public void postInit(File originalFile){
        if (this.initalized)
            return;
        this.initalized = true;
        
        this.originalFile = originalFile;
        
        this.pluginDataStorage = new PluginDataStorage(this);
        this.miniPluginLoader = new MiniPluginLoader(this);
        this.localConfigManager = new ConfigManager(this);
        this.commandManager = new CommandManager(this);
        this.serverManager = new ServerManager(this);
        this.decorator = new ServerDecorator(this);

        initMiniPlugin();
    }

    private void initMiniPlugin() {
        MiniPluginLoader loader = getMiniPluginLoader();
        loader.addMiniPlugin(new Explosion());
        loader.addMiniPlugin(new EntityBlood());
        loader.addMiniPlugin(new FastCombat());
        loader.addMiniPlugin(new DropCounter());
        loader.addMiniPlugin(new ServerMotd());
        loader.addMiniPlugin(new DamageHologram());
        loader.addMiniPlugin(new JukeboxPlay());
    }

    @Override
    public void onEnable() {
        if (!isInitalized()) {
            try {
                File pluginRef = new File(URLDecoder.decode(StoryPlugin.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));

                try {
                    getLogger().log(Level.INFO, "임시 폴더에 복사중... " + getTempStorage().getPath());
                    getTempStorage().saveSync(Files.readAllBytes(pluginRef.toPath()), TEMP_FILE_NAME);

                    getServer().getPluginManager().disablePlugin(this);

                    Plugin plugin = getServer().getPluginManager().loadPlugin(getTempStorage().getPath().resolve(TEMP_FILE_NAME).toFile());
                    Reflect.getMethod(plugin.getClass(), "postInit", File.class).invoke(plugin, pluginRef);

                    if (Reflect.getField(plugin, "initalized").equals(false))
                        throw new Exception("플러그인이 pre init 되지 않았습니다");

                    getServer().getPluginManager().enablePlugin(plugin);

                    ((List<Plugin>) Reflect.getField(getServer().getPluginManager(), "plugins").get(getServer().getPluginManager())).remove(this);
                    ((Map<String, Plugin>)Reflect.getField(getServer().getPluginManager(), "lookupNames").get(getServer().getPluginManager())).remove(getName());

                    ClassLoader cl = getClass().getClassLoader();
                    Reflect.getField(cl, "plugin").set(cl, null);
                    Reflect.getField(cl, "pluginInit").set(cl, null);

                    System.gc();
                } catch (Exception e) {
                    getLogger().warning("플러그인 임시 복사를 실패 했습니다. " + e.getLocalizedMessage());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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

    public ServerManager getServerManager() {
        return serverManager;
    }

    public TempStorage getTempStorage() {
        return tempStorage;
    }
    
    public File getOriginalFile(){
        return originalFile;
    }

    public ServerDecorator getDecorator() {
        return decorator;
    }

    public String getServerName(){
        return ChatColor.GREEN + "Story Network";
    }

    public static void main(String[] args){
        System.out.println("이 프로그램은 단독 실행 될수 없습니다");
    }
}
