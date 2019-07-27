package com.storycraft.helper;

import com.mojang.brigadier.ParseResults;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.MiniPlugin;
import com.storycraft.server.plugin.ServerPluginManager;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class IngamePluginManager extends MiniPlugin implements ICommand {

    private Reflect.WrappedMethod<File, JavaPlugin> getFileMethod;

    @Override
    public void onLoad(StoryPlugin plugin) {
        this.getFileMethod = Reflect.getMethod(JavaPlugin.class, "getFile");
        plugin.getCommandManager().addCommand(this);
    }

    public void onEnable() {

    }

    public ServerPluginManager getServerPluginManager() {
        return getPlugin().getServerManager().getServerPluginManager();
    }

    @Override
    public String[] getAliases() {
        return new String[] {"plugin"};
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.ingameplugin";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin <enable/load/update//disable/remove/reload>"));
            return;
        }

        ServerPluginManager serverPluginManager = getServerPluginManager();

        switch (args[0]) {
            case "load":
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin load <플러그인 파일 웹 주소>"));
                    return;
                }

                File pluginFolder = getPlugin().getOriginalDataFolder().getParentFile();
                File pluginFile = new File(pluginFolder, UUID.randomUUID() + ".jar");

                AsyncTask<Void> task = new AsyncTask<>(new AsyncTask.AsyncCallable<Void>() {
                    @Override
                    public Void get() throws Throwable {
                        URL url = new URL(args[1]);

                        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                        FileOutputStream fos = new FileOutputStream(pluginFile);

                        long count = 0;
                        while ((count = fos.getChannel().transferFrom(rbc, count, Long.MAX_VALUE)) != 0);

                        return null;
                    }
                });

                task.then((Void result, Throwable throwable) -> {
                    try {
                        if (throwable != null)
                            throw throwable;

                        Plugin plugin = serverPluginManager.loadPlugin(pluginFile);
                        sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + plugin.getName() + " 이(가) 로드 되었습니다"));
                    } catch (Throwable t) {
                        sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 로드가 실패 했습니다 " + t.getLocalizedMessage()));
                    }
                });

                task.run();

                break;

            case "update":
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin update <플러그인 파일 웹 주소>"));
                    return;
                }

                File corePluginFile = getPlugin().getOriginalFile();
                File tempDownloadFile = getPlugin().getTempStorage().getPath().resolve("tempPlugin").toFile();
                File tmpBackup = getPlugin().getFile();

                AsyncTask<Void> coreUpdateTask = new AsyncTask<>(new AsyncTask.AsyncCallable<Void>() {
                    @Override
                    public Void get() throws Throwable {
                        URL url = new URL(args[1]);

                        Path downloadPath = tempDownloadFile.toPath();

                        Files.deleteIfExists(downloadPath);
                        Files.createFile(downloadPath);

                        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
                            try (FileOutputStream fos = new FileOutputStream(downloadPath.toFile())) {

                                long count = 0;
                                while ((count = fos.getChannel().transferFrom(rbc, count, Long.MAX_VALUE)) != 0);
    
                            }
                        }

                        return null;
                    }
                });

                coreUpdateTask.then((Void result, Throwable throwable) -> {
                    try {
                        if (throwable != null)
                            throw throwable;

                        String originalName = getPlugin().getName();

                        serverPluginManager.unloadPlugin(getPlugin());

                        Files.deleteIfExists(corePluginFile.toPath());
                        Files.move(tempDownloadFile.toPath(), corePluginFile.toPath());

                        Plugin plugin = serverPluginManager.loadPlugin(corePluginFile);

                        if (plugin == null) {
                            throw new Exception("Plugin load phase failed");
                        }

                        if (!plugin.getName().equals(originalName)) {
                            throw new Exception("Illegal plugin file");
                        }

                        boolean enabled = serverPluginManager.enablePlugin(plugin);

                        if (!enabled) {
                            throw new Exception("Plugin enable phase failed");
                        }

                        sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "코어 플러그인이 업데이트 되었습니다"));
                    } catch (Throwable t) {
                        sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager",
                            "플러그인 로드가 실패 했습니다 " + t.getLocalizedMessage()));
                        try {
                            if (!serverPluginManager.isEnabled(getPlugin())) {
                                Files.deleteIfExists(corePluginFile.toPath());
                                Files.copy(tmpBackup.toPath(), corePluginFile.toPath());

                                Plugin original = serverPluginManager.loadPlugin(corePluginFile);
                                serverPluginManager.enablePlugin(original);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager",
                            "플러그인 재로드가 실패 했습니다 :(" + t.getLocalizedMessage()));
                        }
                    }
                });

                coreUpdateTask.run();

                break;

            case "enable": {
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin enable <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = serverPluginManager.getPlugin(name);

                if (plugin == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin.isEnabled()) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", name + " 은 이미 활성화 되어 있습니다"));
                    return;
                }

                if (serverPluginManager.enablePlugin(plugin))
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 활성화 되었습니다"));
                else
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 활성화가 실패 했습니다"));
                break;
            }

            case "disable": {
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin disable <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = serverPluginManager.getPlugin(name);

                if (plugin == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin == getPlugin()) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "코어 플러그인은 비활성화 할 수 없습니다"));
                    return;
                }

                if (serverPluginManager.disablePlugin(plugin))
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 비활성화 되었습니다"));
                else
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 비활성화가 실패 했습니다"));
                break;
            }

            case "reload": {
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin reload <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = serverPluginManager.getPlugin(name);

                if (plugin == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                }
                else if (plugin == getPlugin()) {
					sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.ALERT, "PluginManager", "코어 플러그인 업데이트중..."));
                    serverPluginManager.unloadPlugin(plugin);
                    serverPluginManager.enablePlugin(serverPluginManager.loadPlugin(getPlugin().getOriginalFile()));
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "코어 플러그인이(가) 업데이트 되었습니다"));
                    return;
                }

                if (serverPluginManager.disablePlugin(plugin) && serverPluginManager.enablePlugin(plugin))
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 리로드 되었습니다"));
                else
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 리로드가 실패 했습니다"));
                break;
            }

            case "remove": {
                if (args.length != 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin remove <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = serverPluginManager.getPlugin(name);

                if (plugin == null) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin == getPlugin()) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "코어 플러그인은 제거 할 수 없습니다"));
                    return;
                }

                if (serverPluginManager.unloadPlugin(plugin)) {
                    getFileMethod.invoke((JavaPlugin) plugin).delete();
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 제거 되었습니다"));
                }
                else
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 제거가 실패 했습니다"));
                break;
            }

            default:
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin <enable/load/disable/remove>"));
                break;
        }
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return false;
    }
}