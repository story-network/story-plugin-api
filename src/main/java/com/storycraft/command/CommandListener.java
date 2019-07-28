package com.storycraft.command;

import com.storycraft.MainMiniPlugin;
import com.storycraft.util.MessageUtil;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandListener extends MainMiniPlugin implements Listener {

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

        String[] parsed = rawCommand.length() != commandStr.length() ? parseArguments(rawCommand.substring(commandStr.length() + 1)) : new String[0];

        List<String[]> list = getExcutableCommandList(sender, parsed);

        for (String[] args : list)
            command.onCommand(sender, args);

        return true;
    }

    public List<String[]> getExcutableCommandList(CommandSender sender, String[] args) {

        List<String[]> list = new ArrayList<>();

        Player nearest = null;

        if (sender instanceof Player) {
            nearest = (Player) sender;
        }
        else if (sender instanceof BlockCommandSender){
            BlockCommandSender bSender = (BlockCommandSender) sender;

            double shortest = Long.MAX_VALUE;

            for (Player p : bSender.getBlock().getLocation().getWorld().getPlayers()) {
                double dq = p.getLocation().distanceSquared(bSender.getBlock().getLocation());
                if (dq < shortest) {
                    shortest = dq;
                    nearest = p;
                }
            }

            if (nearest == null) {
                nearest = getRandomPlayer();
            }
        }

        //FIRST

        List<Integer> allPlayerSelector = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("@p")) {
                args[i] = nearest.getName();
            }
            else if (arg.equals("@r")) {
                args[i] = getRandomPlayer().getName();
            }
            else if (arg.equals("@s")) {
                args[i] = sender.getName();
            }
            else if (args[i].equals("@a")) {
                allPlayerSelector.add(i);
            }
        }

        //SECOND

        if (allPlayerSelector.size() > 0) {
            for (Player p : getPlugin().getServer().getOnlinePlayers()) {
                String[] argsCopy = args.clone();
                
                for (int i : allPlayerSelector) {
                    argsCopy[i] = p.getName();
                }

                list.add(argsCopy);
            }
        }
        else {
            list.add(args);
        }

        return list;
    }

    public Player getRandomPlayer() {
        Collection<? extends Player> list = getPlugin().getServer().getOnlinePlayers();
        return ((Player)list.toArray()[((int) (Math.random() * (list.size() - 1)))]);
    }

    //Legacy
    public String[] parseArguments(String rawArguments) {
        List<String> argList = new ArrayList<>();

        String buffer = "";

        char[] charArray = rawArguments.toCharArray();
        boolean stringMode = false;

        char lastChar = '\0';
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

            lastChar = c;
        }

        if (buffer != "")
            argList.add(buffer);

        return argList.toArray(new String[argList.size()]);
    }
}
