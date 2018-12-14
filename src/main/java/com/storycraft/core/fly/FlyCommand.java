package com.storycraft.core.fly;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends MiniPlugin implements ICommand {

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[] { "fly" };
    }
    
    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.fly";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player p = null;
        if (args.length > 0) {
            String arg = args[0];
            Player target = getPlugin().getServer().getPlayer(arg);

            if (target == null) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Fly", "플레이어 " + arg + " 을(를) 찾을 수 없습니다"));
                return;
            }

            p = target;
        }

        if (p == null) {
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "Fly", "사용법 /fly <플레이어>"));
                return;
            }
        }

        boolean flightMode = !p.getAllowFlight();

        p.setAllowFlight(flightMode);

        if (flightMode) {
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "Fly", "플라이 모드가 활성화 되었습니다"));
        } else {
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "Fly", "플라이 모드가 비활성화 되었습니다"));
        }

        if (!p.equals(sender)) {
            if (flightMode) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "Fly", "플레이어 " + p.getName() + " 의 플라이 모드를 활성화 시켰습니다"));
            }
            else {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "Fly", "플레이어 " + p.getName() + " 의 플라이 모드를 비활성화 시켰습니다"));
            }
        }
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