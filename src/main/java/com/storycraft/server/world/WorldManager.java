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

import net.minecraft.server.v1_13_R2.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldManager extends ServerExtension {

    private boolean isLoaded;
    Map<String, IUniverse> universeList;

    private DefaultUniverse defaultOverworld;
    private DefaultUniverse defaultNether;
    private DefaultUniverse defaultTheEnd;

    public WorldManager(){
        this.universeList = new HashMap<>();
        this.isLoaded = false;
    }

    @Override
    public void onLoad(StoryPlugin plugin){
        this.isLoaded = true;
        //pre set plugin
        setPlugin(plugin);
        loadDefault();
        loadUniverse();
    }

    @Override
    public void onDisable(boolean reload){
        if (!reload)
            unloadAll();
    }

    private void loadDefault() {
        Server server = getPlugin().getServer();
        loadDefaultWorld(defaultOverworld = new DefaultUniverse(server.getWorld("world")));

        if (server.getAllowNether())
            loadDefaultWorld(defaultNether = new DefaultUniverse(server.getWorld("world_nether")));

        if (server.getAllowEnd())
            loadDefaultWorld(defaultTheEnd = new DefaultUniverse(server.getWorld("world_the_end")));
    }

    private void loadUniverse() {
        loadWorld(new TestUniverse("test", 432423));
        loadWorld(new TestUniverse("nt", -126743892));
    }

    public DefaultUniverse getDefaultOverworld() {
        return defaultOverworld;
    }

    public DefaultUniverse getDefaultNether() {
        return defaultNether;
    }

    public DefaultUniverse getDefaultTheEnd() {
        return defaultTheEnd;
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
