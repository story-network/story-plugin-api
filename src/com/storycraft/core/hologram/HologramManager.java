package com.storycraft.core.hologram;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.server.clientside.ClientEntityManager;
import net.minecraft.server.v1_12_R1.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager extends MiniPlugin {

    private static final double HOLOGRAM_OFFSET = 0.15;

    private ClientEntityManager manager;
    private Map<Hologram, List<Entity>> hologramListMap;

    public HologramManager(){
        this.manager = new ClientEntityManager();
        this.hologramListMap = new HashMap<>();
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        plugin.getMiniPluginLoader().addMiniPlugin(manager);
    }

    @Override
    public void onDisable(boolean reload){
        for (Hologram hologram : hologramListMap.keySet()){
            hologram.onRemove();
        }

        hologramListMap.clear();
    }

    public boolean contains(Hologram hologram){
        return hologramListMap.containsKey(hologram);
    }

    public void addHologram(Hologram hologram){
        if (contains(hologram))
            return;

        hologramListMap.put(hologram, new ArrayList<>());
        hologram.onAdd();

        update(hologram);
    }

    public void update(Hologram hologram){
        if (!contains(hologram))
            return;

        List<Entity> textEntityList = hologramListMap.get(hologram);
        String[] textList = hologram.getTextList();

        int listSize = textEntityList.size();
        int textLineCount = textList.length;

        if (listSize < textLineCount) {
            for (int i = listSize; i < textLineCount; i++){
                Entity e = hologram.createHologramEntity();
                manager.addClientEntity(e);
                textEntityList.add(e);
            }
        }
        else if (listSize > textLineCount){
            for (int i = listSize; i > textLineCount; i--){
                Entity e = textEntityList.get(i - 1);
                manager.removeClientEntity(e);
                textEntityList.remove(e);
            }
        }

        for (int i = 0; i < textList.length; i++){
            Entity e = textEntityList.get(i);
            
            e.getBukkitEntity().getLocation().add(0, -i * HOLOGRAM_OFFSET, 0);
            e.setCustomName(textList[i]);

            manager.update(e);
        }
    }

    public void removeHologram(Hologram hologram){
        if (!contains(hologram))
            return;

        for (Entity e : hologramListMap.get(hologram)){
            manager.removeClientEntity(e);
        }

        hologramListMap.remove(hologram);
        hologram.onRemove();
    }

}
