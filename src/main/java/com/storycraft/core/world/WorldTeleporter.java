package com.storycraft.core.world;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.util.MessageUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldTeleporter extends MiniPlugin implements ICommand {

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"wtp"};
    }

    @Override
    public boolean isPermissionRequired() {
	    return true;
    }

    @Override
    public String getPermissionRequired() {
        return "server.command.worldtp";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "WorldTeleporter", "사용법 /wtp <월드 이름> [플레이어 이름] [다른 플레이어 이름]..."));
                return;
            }
        }
        else if (args.length < 2) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "WorldTeleporter", "사용법 /wtp <월드 이름> <플레이어 이름> [다른 플레이어 이름]..."));
            return;
        }

        String name = args[0];
        World w = getPlugin().getServer().getWorld(name);

        if (w == null) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "WorldTeleporter", "월드 " + name + " 를 찾을 수 없습니다"));
            return;
        }

        if (args.length == 1) {
            ((Player)sender).teleport(w.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "WorldTeleporter", name + " 으로 이동 되었습니다"));
        }
        else {
            int count = 0;
            for (int i = 1; i < args.length; i++) {
                Player p = getPlugin().getServer().getPlayer(args[i]);

                if (p != null) {
                    p.teleport(w.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    p.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "WorldTeleporter", sender.getName() + " 에 의해 " + name + " 으로 이동 되었습니다"));
                    count++;
                }
                else {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "WorldTeleporter",  "플레이어 " + args[i] + " 를 찾을 수 없습니다"));
                }
            }

            sender.sendMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "WorldTeleporter",  count + " 플레이어가 이동되었습니다"));
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
