package com.storycraft.core.hologram;

import com.storycraft.StoryPlugin;
import com.storycraft.MiniPlugin;
import com.storycraft.server.clientside.ClientEntityManager;
import com.storycraft.server.packet.AsyncPacketInEvent;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PacketPlayInUseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager extends MiniPlugin implements Listener {

    private static final double HOLOGRAM_OFFSET = 0.75;

    private Map<Hologram, List<Entity>> hologramListMap;

    private WrappedField<Integer, PacketPlayInUseEntity> idField;

    public HologramManager(){
        this.hologramListMap = new HashMap<>();

        this.idField = Reflect.getField(PacketPlayInUseEntity.class, "a");
    }

    @Override
    public void onLoad(StoryPlugin plugin){

    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean reload){
        for (Hologram hologram : hologramListMap.keySet()){
            hologram.onRemove();
        }

        hologramListMap.clear();
    }

    public ClientEntityManager getClientEntityManager() {
        return getPlugin().getServerManager().getClientSideManager().getClientEntityManager();
    }

    @EventHandler
    public void onPacketIn(AsyncPacketInEvent e) {
        if (e.getPacket() instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) e.getPacket();

            int id = idField.get(packet);

            for (Hologram hologram : new ArrayList<>(hologramListMap.keySet())) {
                for (Entity entity : new ArrayList<>(hologramListMap.get(hologram))) {
                    if (entity.getId() == id) {
                        getPlugin().getServer().getPluginManager().callEvent(new HologramInteractEvent(e.getSender(), hologram));
                    }
                }
            }
        }
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

    public List<Entity> getHologramEntityList(Hologram hologram){
        if (!contains(hologram))
            return new ArrayList<>();

        return new ArrayList<>(hologramListMap.get(hologram));
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
                Entity e = hologram.createHologramEntity(i);
                getClientEntityManager().addClientEntity(e);
                textEntityList.add(e);
            }
        }
        else if (listSize > textLineCount){
            for (int i = listSize; i > textLineCount; i--){
                Entity e = textEntityList.get(i - 1);
                getClientEntityManager().removeClientEntity(e);
                textEntityList.remove(e);
            }
        }

        for (int i = 0; i < textList.length; i++){
            Entity e = textEntityList.get(i);

            e.setCustomName(new ChatComponentText(textList[i]));

            getClientEntityManager().update(e);
        }
    }

    public void removeHologram(Hologram hologram){
        if (!contains(hologram))
            return;

        List<Entity> hologramList = hologramListMap.get(hologram);
        for (Entity e : hologramList) {
            getClientEntityManager().removeClientEntity(e);
        }

        hologramListMap.remove(hologram);
        hologram.onRemove();
    }

}
