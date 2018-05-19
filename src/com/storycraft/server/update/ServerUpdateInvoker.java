package com.storycraft.server.update;

import com.storycraft.server.ServerExtension;

public class ServerUpdateInvoker extends ServerExtension {

    private long currentTick;

    public ServerUpdateInvoker(){
        this.currentTick = 0;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run() {
                getPlugin().getServer().getPluginManager().callEvent(new ServerUpdateEvent(currentTick++));
            }
        }, 0, 1);
    }
}
