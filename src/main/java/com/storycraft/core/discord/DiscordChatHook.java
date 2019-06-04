package com.storycraft.core.discord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.AsyncTask.AsyncCallable;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @Override
    public void onDisable(boolean reload) {
        if (!loaded && getWebHookURL().isEmpty())
            return;

        sendConsoleMessageAsync("서버가 중지되고 있습니다.").run();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;

        sendMessageAsync(e.getPlayer().getName(), "https://crafatar.com/avatars/" + e.getPlayer().getPlayerProfile().getId(), e.getMessage()).run();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        sendMessageAsync("Server", " + " + e.getPlayer().getName()).run();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        sendMessageAsync("Server", " - " + e.getPlayer().getName()).run();
    }

    @EventHandler
    public void onPlayerDied(PlayerDeathEvent e) {
        if (e.getDeathMessage() != null)
            sendConsoleMessageAsync(e.getDeathMessage());
    }

    public JsonObject createWebHookObject(String name, String avatarURL, String message) {
        JsonObject object = createWebHookObject(name, message);

        object.addProperty("avatar_url", avatarURL);

        return object;
    }

    public JsonObject createWebHookObject(String name, String message) {
        JsonObject object = new JsonObject();

        object.addProperty("username", name);
        object.addProperty("content", message);

        return object;
    }

    private byte[] encodeJsonObject(JsonObject object) throws UnsupportedEncodingException {
        return new Gson().toJson(object).getBytes("UTF-8");
    }

    protected void onConfigLoaded(Void v, Throwable throwable) {
        loaded = true;

        sendConsoleMessageAsync("서버가 시작되고 있습니다.").run();
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

    public AsyncTask<Void> send(JsonObject webHookObject) {
        return new AsyncTask<Void>(new AsyncCallable<Void>() {
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
                    wr.write(encodeJsonObject(webHookObject));
                    wr.flush();
                    wr.close();
    
                    connection.getInputStream().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
    
                connection.disconnect();
                return null;
            }
        });
    }

    public AsyncTask<Void> sendMessageAsync(String username, String message) {
        if (!loaded || getWebHookURL().isEmpty()) {
            return new AsyncTask<Void>(new AsyncCallable<Void>() {
                @Override
                public Void get() {
                    return null;
                }
            });
        }

        return send(createWebHookObject(username, message));
    }

    public AsyncTask<Void> sendMessageAsync(String username, String avatarURL, String message) {
        if (!loaded || getWebHookURL().isEmpty()) {
            return new AsyncTask<Void>(new AsyncCallable<Void>() {
                @Override
                public Void get() {
                    return null;
                }
            });
        }

        return send(createWebHookObject(username, avatarURL, message));
    }

    public AsyncTask<Void> sendConsoleMessageAsync(String message) {
        return sendMessageAsync("Console", "http://aux2.iconspalace.com/uploads/utilities-terminal-icon-256.png", message);
    }

}