package com.storycraft.command;

import com.storycraft.StoryPlugin;
import com.storycraft.core.IMiniPlugin;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CommandListener implements IMiniPlugin, Listener {
    private CommandManager manager;

    public CommandListener(CommandManager manager){
        this.manager = manager;
    }

    public CommandManager getManager() {
        return manager;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onUnload(boolean reload) {

    }

    //lowest로 설정시 제일 먼저 호출됨
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(AsyncPlayerChatEvent e){
        if (e.isCancelled() || e.getMessage().charAt(0) != CommandManager.COMMAND_PREFIX)
            return;

        e.setCancelled(true);

        String msg = e.getMessage().substring(1);
        //PREFIX 제거 후 공백으로 나눔
        int spaceIndex = msg.indexOf(" ");
        String commandStr = msg.substring(0, spaceIndex == -1 ? spaceIndex - 1 : msg.length());

        ICommand command = getManager().getCommand(commandStr);

        if (command == null){
            e.getPlayer().sendMessage(CommandManager.UNKNOWN_COMMAND);
            return;
        }

        String[] args = msg.substring(commandStr.length()).split(" ");

        command.onCommand(e.getPlayer(), args);
    }
}
