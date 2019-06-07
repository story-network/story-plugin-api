package com.storycraft.server.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.storycraft.core.MiniPlugin;
import com.storycraft.server.world.addon.*;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class WorldAddonManager extends MiniPlugin implements Listener {
    
    private WorldManager manager;

    private Map<String, IWorldAddon> addonMap;

    private Map<String, List<IWorldAddon.AddonHandler>> worldAddonHandlerMap;

    public WorldAddonManager(WorldManager manager) {
        this.addonMap = new HashMap<>();
        this.worldAddonHandlerMap = new HashMap<>();
        this.manager = manager;

        initDefaultAddon();
    }

    @Override
    public void onEnable() {
        for (List<IWorldAddon.AddonHandler> handlerList : worldAddonHandlerMap.values()) {
            for (IWorldAddon.AddonHandler handler : handlerList) {
                getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
            }
        }
    }

    protected void initDefaultAddon() {
        addAddon("NoPhysics", new NoPhysicsAddon());
        addAddon("FixedMob", new FixedMobAddon());
        addAddon("NoFallDamage", new NoFallDamageAddon());
        addAddon("NoFluidPhysics", new NoFluidPhysicsAddon());
        addAddon("SnowPick", new SnowPickAddon());
        addAddon("PearlRide", new PearlRideAddon());
        addAddon("SnowStack", new SnowStackAddon());
    }

    public WorldManager getWorldManager() {
        return manager;
    }

    public boolean contains(String name) {
        return addonMap.containsKey(name);
    }

    public boolean contains(IWorldAddon addon) {
        return addonMap.containsValue(addon);
    }

    public IWorldAddon getByName(String name) {
        if (!contains(name))
            return null;

        return addonMap.get(name);
    }

    public String getName(IWorldAddon addon) {
        if (!contains(addon))
            return null;

        for (String name : addonMap.keySet()) {
            IWorldAddon ad = addonMap.get(name);

            if (ad.equals(addon))
                return name; 
        }

        return null;
    }

    public void addAddon(String name, IWorldAddon addon) {
        if (contains(addon)) {
            if (contains(name))
                removeAddon(name);
            else
                return;
        }

        addonMap.put(name, addon);
        
    }

    public void removeAddon(String name) {
        if (!contains(name))
            return;

        addonMap.remove(name);
    }

    protected List<IWorldAddon.AddonHandler> getAddonHandlerList(World w) {
        List<IWorldAddon.AddonHandler> list;

        if (!worldAddonHandlerMap.containsKey(w.getName())) {
            list = new ArrayList<>();
            
            worldAddonHandlerMap.put(w.getName(), list);
        }
        else
            list = worldAddonHandlerMap.get(w.getName());

        return list;
    }

    public void addAddonToWorld(World w, String name) {
        if (!contains(name))
            return;
        
        addAddonToWorld(w, getByName(name));
    }

    public void addAddonToWorld(World w, IWorldAddon addon) {
        if (hasAddonToWorld(w, addon))
            return;

        IWorldAddon.AddonHandler handler = addon.createHandler(getPlugin(), w);

        getAddonHandlerList(w).add(handler);

        if (isEnabled())
            getPlugin().getServer().getPluginManager().registerEvents(handler, getPlugin());
    }

    public boolean hasAddonToWorld(World w, String name) {
        if (!contains(name))
            return false;
        
        return hasAddonToWorld(w, getByName(name));
    }

    public boolean hasAddonToWorld(World w, IWorldAddon addon) {
        if (!contains(addon))
            return false;

        for (IWorldAddon.AddonHandler handler : getAddonHandlerList(w)) {
            if (handler.getAddon().equals(addon))
                return true;
        }

        return false;
    }

    public void removeAddonToWorld(World w, String name) {
        if (!contains(name))
            return;

        removeAddonToWorld(w, getByName(name));
    }

    public void removeAddonToWorld(World w, IWorldAddon addon) {
        if (!hasAddonToWorld(w, addon))
            return;

        List<IWorldAddon.AddonHandler> handlerList = getAddonHandlerList(w);

        for (IWorldAddon.AddonHandler handler : new ArrayList<>(handlerList)) {
            if (handler.getAddon().equals(addon)) {
                HandlerList.unregisterAll(handler);
                handlerList.remove(handler);
            }
        }
    }

    public void removeAllAddonToWorld(World w) {
        List<IWorldAddon.AddonHandler> handlerList = getAddonHandlerList(w);

        for (IWorldAddon.AddonHandler handler : new ArrayList<>(handlerList)) {
            HandlerList.unregisterAll(handler);
        }

        handlerList.clear();
    }

}