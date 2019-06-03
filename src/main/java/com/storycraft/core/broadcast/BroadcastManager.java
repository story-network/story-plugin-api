package com.storycraft.core.broadcast;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.advancement.AdvancementDisplay;
import com.storycraft.core.advancement.AdvancementType;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BroadcastManager extends MiniPlugin implements ICommand, Listener {

    public static final int BROADCAST_FADE = 250;
    public static final int BROADCAST_TIME = 1500;

    private JsonConfigFile configFile;

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getConfigManager().addConfigFile("broadcast.json", configFile = new JsonConfigFile()).run();
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public String[] getAliases() {
        return new String[] { "broadcast" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Broadcast", "사용법 /broadcast <메세지>"));
            return;
        }

        String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            p.sendTitle(message, getPlugin().getServerName(), BROADCAST_FADE / 20, BROADCAST_TIME / 20, BROADCAST_FADE / 20);
        }

        sender.getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.TIP, "Broadcast", message));
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
        return "server.command.broadcast";
    }
    
}