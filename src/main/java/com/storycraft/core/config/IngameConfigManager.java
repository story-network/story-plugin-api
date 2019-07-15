package com.storycraft.core.config;

import java.io.StringReader;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.IConfigEntry;
import com.storycraft.config.IConfigFile;
import com.storycraft.config.event.ConfigUpdateEvent;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.command.CommandSender;

public class IngameConfigManager extends MiniPlugin implements ICommand {

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "config" };
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.ingameconfig";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config <get/set/merge/save/reload/reload-disk>"));
            return;
        }

        switch (args[0]) {
            case "get": {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config get <config 이름>"));
                    return;
                }

                String name = args[1];

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                IConfigFile file = getPlugin().getConfigManager().getConfigFile(name);

                if (!(file instanceof JsonConfigFile)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "Json 콘픽만 볼 수 있습니다"));
                    return;
                }

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", name + " 파일"));
                sender.sendMessage(((JsonConfigFile) file).getJsonObject().toString());

                break;
            }

            case "set": {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config set <config 이름> <속성> <값>"));
                    return;
                }

                String name = args[1];

                String[] raw = new String[args.length - 3];
                for (int i = 3; i < args.length; i++) {
                    raw[i - 3] = args[i];
                }

                String value = String.join(" ", raw);

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                IConfigFile file = getPlugin().getConfigManager().getConfigFile(name);

                String[] property = args[2].split("\\.");
                int length = property.length - 1;
                int i;
                IConfigEntry<?> entry = (IConfigEntry<?>) file;
                for (i = 0; i < length; i++) {
                    if (entry.getObject(property[i]) == null) {
                        entry.set(property[i], entry = entry.createEntry());
                    }
                    else {
                        entry = entry.getObject(property[i]);
                    }
                }

                JsonReader reader = new JsonReader(new StringReader(value));
                JsonElement element = new JsonParser().parse(reader);
                entry.set(property[i], element);
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", args[2] + " 을(를) " + element.toString() + " 로 설정 했습니다"));

                break;
            }

            case "merge": {
                if (args.length < 3) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config merge <config 이름> <값>"));
                    return;
                }

                String name = args[1];
                
                String[] raw = new String[args.length - 2];
                for (int i = 2; i < args.length; i++) {
                    raw[i - 2] = args[i];
                }

                String value = String.join(" ", raw);

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                IConfigEntry file = (IConfigEntry) getPlugin().getConfigManager().getConfigFile(name);

                try {
                    JsonReader reader = new JsonReader(new StringReader(value));
                    mergeObject(file, new JsonParser().parse(reader).getAsJsonObject());
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", "콘픽 " + name + " 을(를) 주어진 값과 합쳤습니다"));
                } catch (Exception e) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "잘못된 Json 데이터입니다"));
                }
                break;
            }

            case "save": {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config save <config 이름>"));
                    return;
                }

                String name = args[1];

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                getPlugin().getConfigManager().saveConfig(name);
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", "콘픽 " + name + " 을(를) 기록했습니다"));
                break;
            }

            case "reload": {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config reload <config 이름>"));
                    return;
                }

                String name = args[1];

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                getPlugin().getServer().getPluginManager().callEvent(new ConfigUpdateEvent(name, getPlugin().getConfigManager().getConfigFile(name)));
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", "콘픽 " + name + " 을(를) 리로드 했습니다"));
                break;
            }

            case "reload-disk": {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config reload-disk <config 이름>"));
                    return;
                }

                String name = args[1];

                if (!getPlugin().getConfigManager().hasConfigFile(name)) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "해당 콘픽을 찾을 수 없습니다"));
                    return;
                }

                getPlugin().getConfigManager().reloadConfig(name).then((Void result, Throwable throwable) -> {
                    getPlugin().getServer().getPluginManager().callEvent(new ConfigUpdateEvent(name, getPlugin().getConfigManager().getConfigFile(name)));
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", "콘픽 " + name + " 을(를) 저장소에서 리로드 했습니다"));
                }).run();

                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ConfigManager", "콘픽 " + name + " 을(를) 저장소에서 리로드 했습니다"));
                break;
            }

            default:
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ConfigManager", "사용법 /config <get/set/merge/save/reload/reload-disk>"));
                break;
        }
    }

    protected void mergeObject(IConfigEntry entry, JsonObject object) {
        for (Entry<String, JsonElement> element : object.entrySet()) {
            if (element.getValue() instanceof JsonObject) {
                IConfigEntry childEntry;
                if ((childEntry = entry.getObject(element.getKey())) == null) {
                    childEntry = entry.createEntry();
                    entry.set(element.getKey(), childEntry);
                }
                mergeObject(childEntry, object.get(element.getKey()).getAsJsonObject());
            }
            else {
                entry.set(element.getKey(), element.getValue());
            }
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