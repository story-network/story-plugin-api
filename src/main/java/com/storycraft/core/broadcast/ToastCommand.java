package com.storycraft.core.broadcast;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.MiniPlugin;
import com.storycraft.core.advancement.AdvancementType;
import com.storycraft.util.MessageUtil;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToastCommand extends MiniPlugin implements ICommand {

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[]{ "toast" };
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Toast", "사용법 /toast <플레이어 이름> <goal / task / challenge> <아이콘> <메세지>"));
            return;
        }

        String targetPlayer = args[0];

        Player pl = getPlugin().getServer().getPlayer(targetPlayer);
        if (pl == null) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Toast", "플레이어 " + targetPlayer + " 을(를) 찾을 수 없습니다"));
            return;
        }

        AdvancementType type;

        switch (args[1]) {
            case "goal":
                type = AdvancementType.GOAL;
                break;

            case "task":
                type = AdvancementType.TASK;
                break;

            case "challenge":
                type = AdvancementType.CHALLENGE;
                break;

            default:
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Toast", "알 수 없는 토스트 타입입니다"));
                return;
        }

        Material icon = Material.getMaterial(args[2]);

        if (icon == null) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "Toast", "알 수 없는 토스트 아이콘입니다"));
            return;
        }

        String[] messageList = new String[args.length - 3];

        for (int i = 3; i < args.length; i++) {
            messageList[i - 3] = args[i];
        }

        String message = String.join(" ", messageList);

        getPlugin().getDecorator().getAdvancementManager().sendToastToPlayer(pl, message, type, new ItemStack(icon));
        sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "Toast", "토스트가 " + pl.getName() + " 에게 전송되었습니다"));
    }

    @Override
    public boolean availableOnConsole() {
        return true;
    }

    @Override
    public boolean availableOnCommandBlock() {
        return true;
    }
    
    @Override
    public boolean isPermissionRequired() {
        return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.toast";
    }

}