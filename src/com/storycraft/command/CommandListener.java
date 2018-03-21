package com.storycraft.command;

import com.storycraft.core.MiniPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CommandListener extends MiniPlugin implements Listener {
    private CommandManager manager;

    public CommandListener(CommandManager manager){
        this.manager = manager;
    }

    public CommandManager getManager() {
        return manager;
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    //lowest로 설정시 제일 먼저 호출됨
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(AsyncPlayerChatEvent e){
        if (e.isCancelled() || CommandManager.COMMAND_PREFIX.equals(e.getMessage()))
            return;

        e.setCancelled(true);

        String msg = e.getMessage().substring(1);
        //PREFIX 제거 후 공백으로 나눔
        int spaceIndex = msg.indexOf(" ");
        String commandStr = msg.substring(CommandManager.COMMAND_PREFIX.length(), spaceIndex != -1 ? spaceIndex - 1 : msg.length());

        ICommand command = getManager().getCommand(commandStr);

        if (command == null){
            return;
        }

        String[] args = msg.substring(commandStr.length()).split(" ");

        command.onCommand(e.getPlayer(), args);
    }
}
