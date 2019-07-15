package com.storycraft.core.faq;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.MiniPlugin;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FAQCommand extends MiniPlugin implements ICommand {

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("faq.json", configFile = new JsonConfigPrettyFile()).run();
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.faq";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "faq" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Info", "사용법 /faq <faq 이름>"));
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Info", "faq 목록: " + getFAQList()));
            return;
        }

        String faq = args[0];

        if (!configFile.contains(faq)) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Info", "알수 없는 faq 입니다"));
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Info", "faq 목록: " + getFAQList()));
        }

        try {
            if (sender instanceof Player) {//Let ppl know who did this command
                ((Player) sender).chat(ChatColor.GRAY + "/faq " + String.join(" ", args));
            }

            sender.getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "Info", configFile.get(faq).getAsString()));
        } catch (Exception e) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Info", "알수 없는 오류가 발생했습니다"));
        }
    }

    public String getFAQList() {
        Set<Entry<String, JsonElement>> entrySet = configFile.getJsonObject().entrySet();
        String[] list = new String[entrySet.size()];

        int i = 0;
        for (Entry<String, JsonElement> entry : entrySet) {
            list[i++] = entry.getKey();
        }

        return String.join(", ", list);
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
		return true;
	}

}