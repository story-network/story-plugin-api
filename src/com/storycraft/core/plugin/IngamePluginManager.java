package com.storycraft.core.plugin;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.plugin.ServerPluginManager;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.reflect.Reflect;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
    public int getRequiredRankLevel() {
        return ServerRank.DEVELOPER.getRankLevel();
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin <enable/load/disable/unload/reload>"));
            return;
        }

        switch (args[0]) {
            case "load":
                if (args.length != 2) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin load <플러그인 파일 웹 주소>"));
                    return;
                }

                File pluginFolder = getPlugin().getOriginalDataFolder().getParentFile();
                File pluginFile = new File(pluginFolder, UUID.randomUUID() + ".jar");

                AsyncTask task = new AsyncTask<>(new AsyncTask.AsyncCallable<Void>() {
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

                task.then((Object result, Throwable throwable) -> {
                    try {
                        Plugin plugin = getServerPluginManager().loadPlugin(pluginFile);
                        player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + plugin.getName() + " 이(가) 로드 되었습니다"));
                    } catch (Exception e) {
                        player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 로드가 실패 했습니다 " + e.getLocalizedMessage()));
                    }
                });

                task.run();

                break;

            case "enable": {
                if (args.length != 2) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin enable <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = getServerPluginManager().getPlugin(name);

                if (plugin == null) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin.isEnabled()) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", name + " 은 이미 활성화 되어 있습니다"));
                    return;
                }

                if (getServerPluginManager().enablePlugin(plugin))
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 활성화 되었습니다"));
                else
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 활성화가 실패 했습니다"));
                break;
            }

            case "disable": {
                if (args.length != 2) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin disable <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = getServerPluginManager().getPlugin(name);

                if (plugin == null) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin == getPlugin()) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "코어 플러그인은 비활성화 할 수 없습니다"));
                    return;
                }

                if (getServerPluginManager().disablePlugin(plugin))
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 비활성화 되었습니다"));
                else
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 비활성화가 실패 했습니다"));
                break;
            }

            case "reload": {
                if (args.length != 2) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin reload <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = getServerPluginManager().getPlugin(name);

                if (plugin == null) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                }
                else if (plugin == getPlugin()) {
                    getServerPluginManager().unloadPlugin(plugin);
                    getServerPluginManager().enablePlugin(getServerPluginManager().loadPlugin(getPlugin().getOriginalFile()));
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "코어 플러그인이(가) 업데이트 되었습니다"));
                    return;
                }

                if (getServerPluginManager().disablePlugin(plugin) && getServerPluginManager().enablePlugin(plugin))
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 리로드 되었습니다"));
                else
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 리로드가 실패 했습니다"));
                break;
            }

            case "unload": {
                if (args.length != 2) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin unload <플러그인 이름>"));
                    return;
                }

                String name = args[1];
                Plugin plugin = getServerPluginManager().getPlugin(name);

                if (plugin == null) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 을 찾을 수 없습니다"));
                    return;
                } else if (plugin == getPlugin()) {
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "코어 플러그인은 언로드 할 수 없습니다"));
                    return;
                }

                if (getServerPluginManager().unloadPlugin(plugin)) {
                    getFileMethod.invoke((JavaPlugin) plugin).delete();
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "PluginManager", "플러그인 " + name + " 이(가) 언로드 되었습니다"));
                }
                else
                    player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "플러그인 " + name + " 언로드가 실패 했습니다"));
                break;
            }

            default:
                player.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "PluginManager", "사용법 /plugin <enable/load/disable/unload>"));
                break;
        }
    }
}
