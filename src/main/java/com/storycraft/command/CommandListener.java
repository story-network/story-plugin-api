package com.storycraft.command;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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
    public void onCommand(PlayerCommandPreprocessEvent e){
        if (e.isCancelled() || e.getMessage() == null)
            return;

        String msg = e.getMessage().substring(1);
        //PREFIX 제거 후 공백으로 나눔
        String commandStr = msg.split(" ")[0];

        ICommand command = getManager().getCommand(commandStr);

        if (command == null){
            return;
        }

        e.setCancelled(true);

        if (getPlugin().getRankManager().getRank(e.getPlayer()).getRankLevel() < command.getRequiredRankLevel()) {
            e.getPlayer().sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "CommandManager", "권한 레벨이 " + command.getRequiredRankLevel() + " 이상 필요 합니다"));
            return;
        }

        String[] args = msg.length() != commandStr.length() ? parseArguments(msg.substring(commandStr.length() + 1)) : new String[0];

        command.onCommand(e.getPlayer(), args);
    }

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
            } else if (c == '"') {
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
