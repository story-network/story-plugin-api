package com.storycraft.server.tick;

import com.storycraft.server.ServerExtension;

public class TickEventInvoker extends ServerExtension {

    private long currentTick;

    public TickEventInvoker(){
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
                getPlugin().getServer().getPluginManager().callEvent(new ServerAsyncTickEvent(currentTick++));
            }
        }, 0, 1);
    }
}
