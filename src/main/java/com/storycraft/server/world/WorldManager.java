package com.storycraft.server.world;

import com.storycraft.StoryPlugin;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.world.universe.TestUniverse;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import net.minecraft.server.v1_13_R2.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorldManager extends ServerExtension implements Listener {

    private boolean isLoaded;
    Map<String, IUniverse> universeList;

    public WorldManager(){
        this.universeList = new HashMap<>();
        this.isLoaded = false;
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        this.isLoaded = true;
        //pre set plugin
        setPlugin(plugin);
        loadUniverse();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable(boolean reload){
        if (!reload)
            unloadAll();
    }

    @EventHandler
    public void onWorldLoad(WorldInitEvent e) {
        World w = e.getWorld();

        if (contains(w.getName())) {
            return;
        }

        loadDefaultWorld(new DefaultUniverse(w));
    }

    private void loadUniverse() {
        //loadWorld(new TestUniverse("lobby", 0));
    }

    public DefaultUniverse getDefaultOverworld() {
        return (DefaultUniverse) getByName("world");
    }

    public DefaultUniverse getDefaultNether() {
        return (DefaultUniverse) getByName("world_nether");
    }

    public DefaultUniverse getDefaultTheEnd() {
        return (DefaultUniverse) getByName("world_the_end");
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
        universeList.putIfAbsent(universe.getName(), universe);
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
