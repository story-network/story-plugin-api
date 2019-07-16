package com.storycraft.effect;

import org.bukkit.Server;

public interface IEffect {

    long getStartTime();

    boolean isPlaying();
    void stop();
    void play(Server server);
    
}