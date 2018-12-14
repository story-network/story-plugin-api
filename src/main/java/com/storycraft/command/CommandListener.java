package com.storycraft.command;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.MessageUtil;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import net.minecraft.server.v1_13_R2.CommandBossBar;

import java.util.ArrayList;
import java.util.List;

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
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        e.setCancelled(onCommandSent(e.getPlayer(), e.getMessage().substring(1)));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent e){
        e.setCancelled(onCommandSent(e.getSender(), e.getCommand()));
    }

    private boolean onCommandSent(CommandSender sender, String rawCommand) {
        if (rawCommand == null)
            return false;

        //PREFIX 제거 후 공백으로 나눔
        String commandStr = rawCommand.split(" ")[0];

        ICommand command = getManager().getCommand(commandStr);

        if (command == null){
            return false;
        }

        if (sender instanceof Player && command.isPermissionRequired() && !((Player) sender).hasPermission(command.getPermissionRequired())) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "CommandManager", command.getPermissionRequired() + " 권한이 필요 합니다"));
            return true;
        }
        else if (sender instanceof ConsoleCommandSender && !command.availableOnConsole() || sender instanceof BlockCommandSender && !command.availableOnCommandBlock()) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "CommandManager", "콘솔 또는 커맨드 블록에서 사용 불가능한 커맨드 입니다"));
            return true;
        }

        String[] args = rawCommand.length() != commandStr.length() ? parseArguments(rawCommand.substring(commandStr.length() + 1)) : new String[0];

        command.onCommand(sender, args);

        return true;
    }

    //Legacy
    public String[] parseArguments(String rawArguments) {
        List<String> argList = new ArrayList<>();

        String buffer = "";

        char[] charArray = rawArguments.toCharArray();
        boolean stringMode = false;

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];

            if (c == '\\') {
                buffer += charArray[++i];
                continue;
            } else if (c == '\'') {
                stringMode = !stringMode;
                continue;
            }

            if (stringMode) {
                buffer += c;
            }
            else {
                if (c == ' ') {
                    argList.add(buffer);
                    buffer = "";
                } else {
                    buffer += c;
                }
            }
        }

        if (buffer != "")
            argList.add(buffer);

        return argList.toArray(new String[argList.size()]);
    }
}
