package com.storycraft.server.world;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.storycraft.MainPlugin;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.world.universe.BuildUniverse;
import com.storycraft.server.world.universe.TestUniverse;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager extends ServerExtension implements Listener {

    private boolean isLoaded;
    Map<String, IUniverse> universeList;

    private WorldAddonManager addonManager;

    public WorldManager(){
        this.universeList = new HashMap<>();
        this.addonManager = new WorldAddonManager(this);
        this.isLoaded = false;
    }

    @Override
    public void onLoad(MainPlugin plugin){
        this.isLoaded = true;

        plugin.getMiniPluginLoader().addMiniPlugin(addonManager);
        
        //pre set plugin
        setPlugin(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(addonManager, getPlugin());

        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), new Runnable() {
            public void run(){
                for (World w : getPlugin().getServer().getWorlds()) {
                    if (contains(w.getName())) {
                        return;
                    }
            
                    loadDefaultWorld(new DefaultUniverse(w));
                }
            }
        }, 0);
    }

    @Override
    public void onDisable(boolean reload){
        if (!reload)
            unloadAll();
    }

    public WorldAddonManager getAddonManager() {
        return addonManager;
    }

    public IUniverse getByName(String name){
        return universeList.get(name);
    }

    public boolean contains(String name){
        return universeList.containsKey(name);
    }

    public boolean contains(IUniverse universe){
        return universeList.containsValue(universe);
    }

    protected void loadDefaultWorld(DefaultUniverse universe) {
        if (!isLoaded)
            return;
        
        universeList.putIfAbsent(universe.getName(), universe);
    }

    protected void unloadWorldAddon(IUniverse universe) {
        if (!universe.isLoaded())
            return;

        getAddonManager().removeAllAddonToWorld(universe.getBukkitWorld());
    }

    public void loadWorld(CustomUniverse universe){
        if (universe.isLoaded() || !isLoaded)
            return;

        WorldCreator creator = new WorldCreator(universe.getName())
                .environment(universe.getEnvironment()).type(universe.getWorldType()).generateStructures(universe.isStructureGen()).seed(universe.getSeed());

        if (universe.hasCustomGenerator())
            creator.generator(universe.getChunkGenerator());

        if (getPlugin().getServer().getWorld(universe.getName()) != null) {
            getPlugin().getServer().unloadWorld(universe.getName(), true);
        }
        
        World w = getPlugin().getServer().createWorld(creator);

        universeList.putIfAbsent(universe.getName(), universe);
        universe.load(w);
    }

    public void unloadWorld(CustomUniverse universe){
        if (!universe.isLoaded() || !isLoaded || !universeList.remove(universe.getName(), universe))
            return;

        for (Chunk chunk : universe.getBukkitWorld().getLoadedChunks()){
            chunk.unload(universe.canSave());
        }

        universe.unload();
        unloadWorldAddon(universe);

        getPlugin().getServer().unloadWorld(universe.getBukkitWorld(), universe.canSave());
    }

    public void unloadAll(){
        Collection<IUniverse> universeSet = new ArrayList<>(universeList.values());
        for (IUniverse universe : universeSet){

            if (universe instanceof CustomUniverse)
                unloadWorld((CustomUniverse) universe);
        }
    }
}
