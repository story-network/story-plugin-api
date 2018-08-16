package com.storycraft.core.hologram;

import net.minecraft.server.v1_13_R1.Entity;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Hologram {

    private Location location;
    private String[] textList;

    public Hologram(Location location, String... texts){
        this.location = location;
        this.textList = texts;
    }

    public String[] getTextList(){
        return textList;
    }

    public void setText(String... texts) {
        textList = texts;
    }

    public Location getLocation(){
        return location;
    }

    public void onInteract(PlayerInteractEvent e){

    }

    public void onAdd() {

    }

    public void onRemove() {

    }

    protected abstract Entity createHologramEntity(int line);
}
