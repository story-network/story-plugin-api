package com.storycraft.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.map.CraftMapView;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class CustomMapTracker {

    private int mapId;

    private List<Player> playerList;

    private BiFunction<CustomMapTracker, Player, Void> onAdded;
    private BiFunction<CustomMapTracker, Player, Void> onRemoved;

    public CustomMapTracker(int mapId) {
        this(mapId, null, null);
    }

    public CustomMapTracker(int mapId, BiFunction<CustomMapTracker, Player, Void> onAdded ,BiFunction<CustomMapTracker, Player, Void> onRemoved) {
        this.mapId = mapId;

        this.onAdded = onAdded;
        this.onRemoved = onRemoved;

        this.playerList = new ArrayList<>();
    }

    public void setOnAdded(BiFunction<CustomMapTracker, Player, Void> onAdded) {
        this.onAdded = onAdded;
    }

    public void setOnRemoved(BiFunction<CustomMapTracker, Player, Void> onRemoved) {
        this.onRemoved = onRemoved;
    }

    public int getMapId() {
        return mapId;
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(playerList);
    }

    public boolean contains(Player p) {
        return playerList.contains(p);
    }

    protected void addTracked(Player p) {
        if (contains(p))
            return;

        if (onAdded != null)
            onAdded.apply(this, p);
        
        playerList.add(p);
    }

    protected void removeTracked(Player p) {
        if (!contains(p))
            return;

        if (onRemoved != null)
            onRemoved.apply(this, p);

        playerList.remove(p);
    }

    public boolean canSeeItemFrame(Player p) {
        for (ItemFrame e : p.getWorld().getEntitiesByClass(ItemFrame.class)) {
            ItemStack item = e.getItem();
            if (e.getLocation().distanceSquared(p.getLocation()) < 16384) {
                if (item != null && item.getType() == Material.FILLED_MAP && item.hasItemMeta()) {
                    MapMeta meta = (MapMeta) item.getItemMeta();
    
                    if (meta.hasMapView() && meta.getMapView().getId() == getMapId())
                        return true;
                }
            }
        }

        return false;
    }

    public boolean canSeeItem(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == Material.FILLED_MAP && item.hasItemMeta()) {
                MapMeta meta = (MapMeta) item.getItemMeta();

                if (meta.hasMapView() && meta.getMapView().getId() == getMapId())
                    return true;
            }
        }

        return false;
    }

    public boolean canSee(Player p) {
        return canSeeItem(p) || canSeeItemFrame(p);
    }

    public void update(Collection<Player> playerList) {
        for (Player p : playerList) {
            boolean flag = contains(p);
            boolean canSee = canSee(p);

            if (flag && !canSee) {
                removeTracked(p);
            }
            else if(!flag && canSee) {
                addTracked(p);
            }
        }
    }

}