package com.storycraft.server.event.server;

import com.storycraft.server.ServerExtension;
import net.minecraft.server.v1_13_R2.MinecraftServer;

public class ServerEventManager extends ServerExtension {

    public ServerEventManager(){

    }

    public long getCurrentTick() {
        return MinecraftServer.currentTick;
    }

    @Override
    public void onEnable(){
        invokeUpdateEvent();
    }

    public void invokeUpdateEvent(){
        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run() {
                getPlugin().getServer().getPluginManager().callEvent(new ServerUpdateEvent(getCurrentTick()));
            }
        }, 0, 1);
    }
}
