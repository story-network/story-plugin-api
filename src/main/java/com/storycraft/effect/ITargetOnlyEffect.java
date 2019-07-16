package com.storycraft.effect;

import org.bukkit.entity.Player;

public interface ITargetOnlyEffect extends IEffect {
    
    Player[] getPlayers();
    
    void play(Player... p);

}