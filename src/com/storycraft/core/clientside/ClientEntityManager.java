package com.storycraft.core.clientside;

import com.storycraft.core.MiniPlugin;
import org.bukkit.event.Listener;

public class ClientEntityManager extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }
}
