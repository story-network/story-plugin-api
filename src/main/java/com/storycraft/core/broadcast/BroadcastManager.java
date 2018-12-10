package com.storycraft.core.broadcast;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
    public int getRequiredRankLevel() {
        return ServerRank.DEVELOPER.getRankLevel();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Broadcast", "?��?���? /broadcast <메세�?>"));
            return;
        }

        String message = String.join(" ", args);

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            p.sendTitle(message, getPlugin().getServerName(), BROADCAST_FADE, BROADCAST_TIME, BROADCAST_FADE);
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
    
}