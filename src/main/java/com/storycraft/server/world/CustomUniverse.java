package com.storycraft.server.world;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public abstract class CustomUniverse implements IUniverse {

    private String name;

    private World world;
    private boolean isLoaded;

    private long seed;
    private boolean structureGen;
    private boolean save;

    public CustomUniverse(String name){
        this.name = name;
        this.isLoaded = false;
        this.structureGen = false;
        this.seed = new Random().nextLong();
        this.save = true;
    }

    public CustomUniverse(String name, long seed){
        this(name);
        this.seed = seed;
    }

    public CustomUniverse(String name, long seed, boolean structureGen){
        this(name, seed);
        this.structureGen = structureGen;
    }

    public CustomUniverse(World world){
        load(world);
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isStructureGen() {
        return structureGen;
    }

    protected void load(World world){
        this.world = world;
        name = world.getName();
        seed = world.getSeed();
        isLoaded = true;
        structureGen = world.canGenerateStructures();

        onLoad();
    }

    protected void unload(){
        onUnload();
    }

    public abstract void onLoad();
    public abstract void onUnload();

    public void setStructureGen(boolean structureGen){
        if (isLoaded())
            return;

        this.structureGen = structureGen;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        if (isLoaded())
            return;

        this.seed = seed;
    }

    public void setSave(boolean flag){
        this.save = flag;
    }

    public boolean canSave(){
        return save;
    }

    public World getBukkitWorld() {
        return world;
    }

    public abstract World.Environment getEnvironment();

    public WorldType getWorldType(){
        return WorldType.NORMAL;
    }

    public boolean hasCustomGenerator(){
        return false;
    }

    public ChunkGenerator getChunkGenerator(){
        return null;
    }
}
