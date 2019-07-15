package com.storycraft.core.teleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.MiniPlugin;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAskCommand extends MiniPlugin implements ICommand {

    public static final int ALLOWED_TIME = 120000;

    private Map<UUID, TeleportRequestInfo> teleportRequest;

    public TeleportAskCommand() {
        this.teleportRequest = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(this);
    }

	@Override
	public String[] getAliases() {
		return new String[] { "tpa" };
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "사용법 /tpa <대상 플레이어 / accept / deny>"));
            return;
        }

        String arg = args[0];

        Player p = (Player) sender;

        if ("accept".equals(arg)) {
            TeleportRequestInfo info = getLastRequest(p);
            
            if (info == null) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "텔레포트 요청이 없습니다"));
                return;
            }

            Player target = getPlugin().getServer().getPlayer(info.getRequester());

            if (System.currentTimeMillis() - info.getTimeRequested() > ALLOWED_TIME) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "텔레포트 가능 시간을 넘었습니다 (120초)"));
            }
            else if (target == null) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "요청자가 오프라인입니다"));
            }
            else {
                target.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "TeleportManager", "이동되었습니다"));
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "TeleportManager", "텔레포트 요청이 수락되었습니다"));
                target.teleport(p);
            }

            teleportRequest.remove(info.getRequester());
        }
        else if ("deny".equals(arg)) {
            TeleportRequestInfo info = getLastRequest(p);
            
            if (info == null) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "텔레포트 요청이 없습니다"));
                return;
            }

            Player target = getPlugin().getServer().getPlayer(info.getRequester());

            if (target != null) {
                target.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "텔레포트 요청이 거절되었습니다"));
            }
            
            p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "TeleportManager", "텔레포트 요청을 거절했습니다"));
            teleportRequest.remove(info.getRequester());
        }
        else {
            Player target = getPlugin().getServer().getPlayer(arg);

            if (target == null) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "플레이어 " + arg + " 를 찾을 수 없습니다"));
                return;
            }

            if (p.equals(target)) {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "TeleportManager", "자기 자신한테 요청을 보낼 수 없습니다"));
                return;
            }

            TeleportRequestInfo info = new TeleportRequestInfo(p.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());

            if (teleportRequest.containsKey(p.getUniqueId())) {
                teleportRequest.replace(p.getUniqueId(), info);
            }
            else {
                teleportRequest.put(p.getUniqueId(), info);
            }

            p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "TeleportManager", arg + " 에게 텔레포트 요청을 보냅니다"));
            target.sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "TeleportManager", p.getName() + " 이 텔레포트 요청을 보냈습니다. /tpa accept 로 수락할 수 있습니다"));
        }
    }
    
    public TeleportRequestInfo getLastRequest(Player p) {
        TeleportRequestInfo lastRequest = null;
        for (TeleportRequestInfo info : teleportRequest.values()) {
            if (p.getUniqueId().equals(info.getTargetUUID())) {
                if (lastRequest == null || info.getTimeRequested() >= lastRequest.getTimeRequested())
                    lastRequest = info;
            }
        }

        return lastRequest;
    }

	@Override
	public boolean availableOnConsole() {
		return false;
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
        return "server.command.tpa";
    }
    
    public class TeleportRequestInfo {

        private UUID requester;
        private UUID target;
        private long timeRequested;

        public TeleportRequestInfo(UUID requester, UUID target, long timeRequested) {
            this.requester = requester;
            this.target = target;
            this.timeRequested = timeRequested;
        }

        public UUID getRequester() {
            return requester;
        }

        public UUID getTargetUUID() {
            return target;
        }

        public long getTimeRequested() {
            return timeRequested;
        }
    }

}