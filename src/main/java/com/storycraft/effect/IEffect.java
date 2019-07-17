package com.storycraft.effect;

public interface IEffect {

    long getStartTime();

    boolean isPlaying();
    void stop();
    void play();
    
}