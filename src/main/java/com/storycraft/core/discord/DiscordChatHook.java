package com.storycraft.core.discord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.AsyncTask.AsyncCallable;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DiscordChatHook extends MiniPlugin implements Listener {

    private JsonConfigFile configFile;

    private boolean loaded;

    public DiscordChatHook() {
        this.loaded = false;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("webhook.json", configFile = new JsonConfigFile()).then(this::onConfigLoaded).run();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;

        if (loaded) {
            if (getWebHookURL().isEmpty())
                return;

            new AsyncTask<Void>(new AsyncCallable<Void>() {
                @Override
                public Void get() {
                    URL url;
                    try {
                        url = new URL(getWebHookURL());
                    } catch (MalformedURLException e2) {
                        getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "DiscordChatHook", "알맞지 않은 url 입니다"));
                        return null;
                    }
                
                    HttpsURLConnection connection;
                    try {
                        connection = (HttpsURLConnection) url.openConnection();
                    } catch (IOException e2) {
                        e2.printStackTrace();
        
                        return null;
                    }
        
                    try {
                        connection.setRequestMethod("POST");
                    } catch (ProtocolException e1) {
                        e1.printStackTrace();
                    }
                
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("User-Agent", "server-chat-webhook");
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
        
                    DataOutputStream wr;
                    try {
                        connection.connect();
        
                        wr = new DataOutputStream(connection.getOutputStream());
                        wr.write(createWebHookObject(e.getPlayer().getName(), e.getMessage()).getBytes("UTF-8"));
                        wr.flush();
                        wr.close();
        
                        connection.getInputStream().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
        
                    connection.disconnect();
                    return null;
                }
            }).run();
        }
    }

    public String createWebHookObject(String name, String chat) {
        JsonObject object = new JsonObject();

        object.addProperty("username", name);
        object.addProperty("content", chat);

        return new Gson().toJson(object);
    }

    protected void onConfigLoaded(Void v, Throwable throwable) {
        loaded = true;
    }

    public String getWebHookURL() {
        try {
            return configFile.get("url").getAsString();
        } catch (Exception e) {
            setWebHookURL("");

            return "";
        }
    }

    public void setWebHookURL(String url) {
        configFile.set("url", url);
    }

}