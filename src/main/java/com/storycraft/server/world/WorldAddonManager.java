package com.storycraft.server.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.MiniPlugin;
import com.storycraft.config.event.ConfigUpdateEvent;
import com.storycraft.server.world.addon.*;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldAddonManager extends MiniPlugin implements Listener {
    
    private WorldManager manager;

    private JsonConfigFile worldAddonConfig;

    private Map<String, IWorldAddon> addonMap;

    private Map<String, List<IWorldAddon.AddonHandler>> worldAddonHandlerMap;

    public WorldAddonManager(WorldManager manager) {
        this.addonMap = new HashMap<>();
        this.worldAddonHandlerMap = new HashMap<>();
        this.manager = manager;

        initDefaultAddon();
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        try {
            plugin.getConfigManager().addConfigFile("world_addons.json", worldAddonConfig = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

    protected JsonArray getWorldAddonArray(World w) {
        try {
            return worldAddonConfig.get(w.getName()).getAsJsonArray();
        } catch (Exception e) {
            JsonArray array;
            setWorldAddonList(w, array = new JsonArray());
            return array;
        }
    }

    protected List<ConfigAddonInfo> getWorldAddonList(World w) {
        JsonArray array = getWorldAddonArray(w);
        JsonArray newArray = new JsonArray();

        List<ConfigAddonInfo> addonInfo = new ArrayList<>();

        array.forEach((JsonElement element) -> {
            ConfigAddonInfo info = null;
            if (element.isJsonPrimitive()) {
                element = convertOldConfigElement(element.getAsString());
            }

            if (element.isJsonObject()) {
                try {
                    JsonObject obj = element.getAsJsonObject();
                    newArray.add(obj);
                    info = new ConfigAddonInfo(obj.get("name").getAsString(), new JsonConfigEntry(obj.get("config").getAsJsonObject()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (info != null)
                addonInfo.add(info);
        });

        setWorldAddonList(w, newArray);

        return addonInfo;
    }

    private JsonObject convertOldConfigElement(String name) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", name);
        obj.add("config", new JsonObject());

        return obj;
    }

    private void setWorldAddonList(World w, JsonArray array) {
        worldAddonConfig.set(w.getName(), array);
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

    public void addAddonToWorld(World w, String name, JsonConfigEntry config) {
        if (!contains(name))
            return;
        
        addAddonToWorld(w, getByName(name), config);
    }

    public void addAllAddonInConfig(World w) {
        for (ConfigAddonInfo info : getWorldAddonList(w)) {
            addAddonToWorld(w, info.getName(), info.getAddonConfigEntry());
        }
    }

    public void addAddonToWorld(World w, IWorldAddon addon, JsonConfigEntry config) {
        IWorldAddon.AddonHandler handler = addon.createHandler(getPlugin(), w, config);

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

    @EventHandler
    public void onConfigReload(ConfigUpdateEvent e) {
        if (worldAddonConfig.equals(e.getConfig())) {
            for (IUniverse universe : getWorldManager().universeList.values()) {
                removeAllAddonToWorld(universe.getBukkitWorld());
                addAllAddonInConfig(universe.getBukkitWorld());
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getWorld() == null) {
            return;
        }
        
        removeAllAddonToWorld(e.getWorld());
        addAllAddonInConfig(e.getWorld());
    }

    class ConfigAddonInfo {

        private JsonConfigEntry addonConfigEntry;
        private String name;

        public ConfigAddonInfo(String name, JsonConfigEntry entry) {
            this.name = name;
            this.addonConfigEntry = entry;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the addonConfigEntry
         */
        public JsonConfigEntry getAddonConfigEntry() {
            return addonConfigEntry;
        }

    }

}