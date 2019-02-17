package com.storycraft.core.server;

import com.destroystokyo.paper.Title;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.spigotmc.WatchdogThread;

import joptsimple.OptionSet;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.World;

public class RestartManager extends MiniPlugin implements ICommand {

    public static final long DEFAULT_INTERVAL = 1000 * 60 * 60 * 32;
    public static final long[] DEFAULT_ALERT_TIME = { 1000, 2000, 3000, 4000, 5000, 10000, 30000, 60000 };

    private JsonConfigFile configFile;

    private long lastStartTime;

    private boolean stopped;

    @Override
    public void onLoad(StoryPlugin plugin) {
        try {
            plugin.getConfigManager().addConfigFile("server_restart.json", configFile = new JsonConfigPrettyFile())
                    .getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        plugin.getCommandManager().addCommand(this);

        lastStartTime = System.currentTimeMillis();
    }

    @Override
    public void onEnable() {
        initScheduler();
    }

    public long getLastStartTime() {
        return lastStartTime;
    }

    public long getRestartInterval() {
        try {
            return configFile.get("interval").getAsLong();
        } catch (Exception e) {
            setRestartInterval(DEFAULT_INTERVAL);

            return DEFAULT_INTERVAL;
        }
    }

    public void setRestartInterval(long interval) {
        configFile.set("interval", interval);
    }

    public void setAlertTimeList(long[] list) {
        configFile.set("alert_time", Lists.newArrayList(list));
    }

    public long[] getAlertTimeList() {
        try {
            JsonArray array = configFile.get("alert_time").getAsJsonArray();
            long[] timeArray = new long[array.size()];

            for (int i = 0; i < timeArray.length; i++) {
                timeArray[i] = array.get(i).getAsLong();
            }

            return timeArray;
        } catch (Exception e) {
            setAlertTimeList(DEFAULT_ALERT_TIME);

            return DEFAULT_ALERT_TIME;
        }
    }

    protected AsyncTask<Boolean> restartServerAsync() {
        stopped = false;
        // commit new thread before plugin thread closed
        return new AsyncTask<Boolean>(new AsyncTask.AsyncCallable<Boolean>() {

            @Override
            public Boolean get() {
                MinecraftServer server = getPlugin().getServerManager().getMinecraftServer();

                long time = System.currentTimeMillis();

                server.postToMainThread(() -> {
                    try {
                        WrappedField<Boolean, MinecraftServer> stopFlagField = Reflect.getField(MinecraftServer.class, "isStopped");

                        WatchdogThread.doStop();
                        stopFlagField.set(server, true);

                        server.stop();

                        if (server.primaryThread.isAlive()) {
                            server.primaryThread.interrupt();
                        }

                        long stoppedTime = System.currentTimeMillis();

                        System.out.println("Server stop task took " + (stoppedTime - time) + " ms");
                        stopped = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                while (!stopped) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }

                OptionSet options = server.options;

                WatchdogThread.hasStarted = false;
                System.gc();

                System.out.println("Restarting server with current properties...");

                WrappedField<WatchdogThread, WatchdogThread> watchdogThread = Reflect.getField(WatchdogThread.class, "instance");

                WrappedField<Long, WatchdogThread> timeoutTime = Reflect.getField(WatchdogThread.class, "timeoutTime");
                WrappedField<Boolean, WatchdogThread> restartField = Reflect.getField(WatchdogThread.class, "restart");

                WrappedField<Server, Bukkit> serverField = Reflect.getField(Bukkit.class, "server");

                WatchdogThread thread = watchdogThread.get(null);

                if (thread.isAlive()) {
                    thread.interrupt();
                }

                watchdogThread.set(null, null);
                serverField.set(null, null);

                MinecraftServer.main(options);

                System.gc();

                return true;
            }

        });
    }

    private void initScheduler() {
        BukkitScheduler scheduler = getPlugin().getServer().getScheduler();

        long restartInterval = getRestartInterval();
        long[] timeList = getAlertTimeList();

        for (long time : timeList) {
            scheduler.runTaskLaterAsynchronously(getPlugin(), () -> {
                getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Restart", (time / 1000) + " 초 후 서버가 재시작됩니다"));

                Title title = new Title(ChatColor.YELLOW + "Server restart", (time / 1000) + " 초 후 서버가 재시작됩니다");

                for (Player p : getPlugin().getServer().getOnlinePlayers()) {
                    p.sendTitle(title);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 1f);
                }
            }, (restartInterval - time) / 50);
        }

        scheduler.runTaskLaterAsynchronously(getPlugin(), this::restartServer, restartInterval / 50);
    }

    @Override
    public String[] getAliases() {
        return new String[] {"restart"};
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Restart", sender.getName() + " 에 의해 10초후 서버가 재시작 됩니다"));
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), this::restartServer, 200);
    }

    private void restartServer() {
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            p.kickPlayer("서버 재시작");
        }

        getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Restart", "서버 재시작중..."));

        restartServerAsync().run();
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return false;
    }

    @Override
    public boolean isPermissionRequired() {
        return true;
    }

    @Override
    public String getPermissionRequired() {
        return "command.server.restart";
    }


}