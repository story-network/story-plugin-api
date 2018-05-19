package com.storycraft.server.clientside;

import com.storycraft.server.ServerExtension;
import net.minecraft.server.v1_12_R1.*;

import java.util.ArrayList;
import java.util.List;

public class ClientEntityManager extends ServerExtension {

    private List<Entity> entityList;

    public ClientEntityManager(){
        this.entityList = new ArrayList<>();
    }

    @Override
    public void onDisable(boolean reload){
        for (Entity e : new ArrayList<>(entityList)){
            removeClientEntity(e);
        }
    }

    public void addClientEntity(Entity e){
        if (entityList.contains(e))
            return;
        entityList.add(e);

        ((WorldServer) e.getWorld()).getTracker().track(e);
    }

    public void removeClientEntity(Entity e){
        if (!entityList.remove(e))
            return;

        ((WorldServer) e.getWorld()).getTracker().untrackEntity(e);
    }
}
