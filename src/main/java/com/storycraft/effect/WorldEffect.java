package com.storycraft.effect;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class WorldEffect implements IWorldEffect, ITargetOnlyEffect {

    private Player[] players;

    private boolean playing;

    private long startTime;

    @Override
    public void play(Server server) {
        if (isPlaying())
            return;

        List<Player> playerList = getWorld().getPlayers();

        play(playerList.toArray(new Player[playerList.size()]));
    }

    @Override
    public void play(Player... playerList) {
        this.startTime = System.currentTimeMillis();
        this.playing = true;
        this.players = playerList;
    }

    @Override
    public void stop() {
        this.startTime = -1;
        this.playing = false;
        this.players = null;
    }

    @Override
    public Player[] getPlayers() {
        return players;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

}