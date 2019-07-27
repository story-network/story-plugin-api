package com.storycraft;

import com.mojang.authlib.yggdrasil.response.User;
import com.storycraft.command.CommandManager;
import com.storycraft.config.ConfigManager;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.helper.IngameConfigManager;
import com.storycraft.helper.IngameModuleManager;
import com.storycraft.helper.IngamePluginManager;
import com.storycraft.MiniPluginLoader;
import com.storycraft.ServerDecorator;
import com.storycraft.server.ServerManager;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.server.plugin.ServerPluginManager;
import com.storycraft.storage.PluginDataStorage;
import com.storycraft.storage.TempStorage;
import com.storycraft.test.TestFunction;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.WorldServer;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainPlugin extends JavaPlugin implements Listener {

    private static String TEMP_FILE_NAME = "Server.jar";

    private File originalFile;
    private File originalDataFolder;

    private JsonConfigFile serverConfig;

    private PluginDataStorage pluginDataStorage;
    private MiniPluginLoader miniPluginLoader;
    private CommandManager commandManager;
    private ConfigManager localConfigManager;
    private ServerManager serverManager;

    private ServerDecorator decorator;

    private boolean initalized = false;

    private DynamicModule module;

    private TempStorage tempStorage;

    public MainPlugin() {
        this.tempStorage = new TempStorage();
        this.initalized = false;
    }

    public void postInit(File originalFile, File originalDataFolder){
        if (this.initalized)
            return;
        this.initalized = true;
        
        this.originalFile = originalFile;
        this.originalDataFolder = originalDataFolder;
        
        this.pluginDataStorage = new PluginDataStorage(this);
        this.miniPluginLoader = new MiniPluginLoader(this);
        this.localConfigManager = new ConfigManager(this);
        this.commandManager = new CommandManager(this);

        try {
            getConfigManager().addConfigFile("server.json", serverConfig = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        this.serverManager = new ServerManager(this);
        this.decorator = new ServerDecorator(this);

        this.module = new DynamicModule();
        getMiniPluginLoader().addMiniPlugin(module);
        getMiniPluginLoader().addMiniPlugin(new IngamePluginManager());
        getMiniPluginLoader().addMiniPlugin(new IngameConfigManager());
        getMiniPluginLoader().addMiniPlugin(new IngameModuleManager());
        
        registerMiniPlugin(getMiniPluginLoader());

        registerCommand(getCommandManager());
    }

    public DynamicModule getModuleManager() {
        return module;
    }

    protected void registerMiniPlugin(MiniPluginLoader loader) {

    }

    protected void registerCommand(CommandManager manager) {

    }

    @Override
    public void onEnable() {
        if (!isInitalized()) {
            Server server = getServer();
            Logger logger = getLogger();

            File originalPluginFile = getOriginalFile() == null ? getFile() : getOriginalFile();
            File originalDataFolder = getDataFolder();

            Function<Throwable, ? super Void> onFailed = (Throwable throwable) -> {
                try {
                    logger.warning("임시 파일 복사 및 로드가 실패 했습니다. " + throwable.getLocalizedMessage());
                    logger.warning("플러그인 복사를 스킵합니다.");
                    Plugin plugin = getServer().getPluginManager().loadPlugin(originalPluginFile);
                    Reflect.getMethod(plugin.getClass(), "postInit", File.class, File.class).invoke(plugin, originalPluginFile, originalDataFolder);

                    if (Reflect.getField(plugin, "initalized").get(plugin).equals(false)) {
                        logger.warning("플러그인 로드가 실패 했습니다.");
                        return null;
                    }

                    server.getPluginManager().enablePlugin(plugin);
                } catch (Exception e) {
                    logger.warning("플러그인 로드가 실패 했습니다.");
                }

                return null;
            };

            try {
                File pluginRef = new File(URLDecoder.decode(MainPlugin.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));

                ServerPluginManager tempPluginManager = new ServerPluginManager(this);

                getLogger().log(Level.INFO, "임시 폴더에 복사중... " + getTempStorage().getPath());
                getTempStorage().saveSync(Files.readAllBytes(pluginRef.toPath()), TEMP_FILE_NAME);

                tempPluginManager.unloadPlugin(this);

                try {
                    Plugin plugin = getServer().getPluginManager().loadPlugin(getTempStorage().getPath().resolve(TEMP_FILE_NAME).toFile());
                    Reflect.getMethod(plugin.getClass(), "postInit", File.class, File.class).invoke(plugin, originalPluginFile, originalDataFolder);

                    if (Reflect.getField(plugin, "initalized").get(plugin).equals(false)) {
                        throw new Exception("플러그인이 pre init 되지 않았습니다");
                    }

                    server.getPluginManager().enablePlugin(plugin);
                } catch (Exception e) {
                    onFailed.apply(e);
                }
            } catch (IOException e) {
                onFailed.apply(e);
            }
        }
        else{
            getMiniPluginLoader().onEnable();

            getServer().getPluginManager().registerEvents(this, this);

            new TestFunction(this);
        }
    }

    public org.bukkit.World getDefaultWorld() {
        return getServer().getWorld("world");
    }

    @Override
    public void onDisable() {
        if (isInitalized()) {
            getMiniPluginLoader().onDisable(false);
        }
    }

    public void reloadPlugin() {
        if (!isInitalized())
            return;

        ServerPluginManager pluginManager = getServerManager().getServerPluginManager();
        pluginManager.unloadPlugin(this);
        pluginManager.enablePlugin(pluginManager.loadPlugin(getOriginalFile()));
    }

    public boolean isInitalized() {
        return initalized;
    }

    public ConsoleCommandSender getConsoleSender() {
        return getServer().getConsoleSender();
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

    @Override
    public File getFile() {
        return super.getFile();
    }

    public File getOriginalDataFolder() {
        return originalDataFolder;
    }

    public ServerDecorator getDecorator() {
        return decorator;
    }

    public JsonConfigFile getServerConfig() {
        return serverConfig;
    }

    public String getServerName(){
        try {
            return serverConfig.get("server-name").getAsString();
        } catch (Exception e) {
            String defaultName = ChatColor.GREEN + "@";

            serverConfig.set("server-name", defaultName);

            return defaultName;
        }
    }

    public String getServerHomepage(){
        try {
            return serverConfig.get("server-web").getAsString();
        } catch (Exception e) {
            String defaultURL = "https://";

            serverConfig.set("server-web", defaultURL);

            return defaultURL;
        }
    }

    public static void main(String[] args){
        System.out.println("이 프로그램은 단독 실행 될수 없습니다");
    }

    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.println("Story Server Preloaded");
    }
}
