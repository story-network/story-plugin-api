package com.storycraft.server.clientside;

import com.storycraft.server.ServerExtension;
import com.storycraft.server.event.client.AsyncPlayerLoadChunkEvent;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.PacketUtil;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientBlockManager extends ServerExtension implements Listener {

    private Map<Location, BlockData> blockMap;

    private WrappedField<BlockPosition, PacketPlayOutBlockChange> positionField;

    public ClientBlockManager(){
        this.blockMap = new HashMap<>();

        this.positionField = Reflect.getField(PacketPlayOutBlockChange.class, "a");
    }

    @Override
    public void onDisable(boolean reload){
        for (Location loc : new ArrayList<>(blockMap.keySet())) {
            removeClientBlockInternal(loc);
        }

        blockMap.clear();
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void addClientBlock(Location loc, BlockData data){
        Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, 0);

        if (contains(blockLoc))
            return;

        addClientBlockInternal(blockLoc, data);
    }

    protected void addClientBlockInternal(Location loc, BlockData data){
        blockMap.put(loc, data);
        sendBlockUpdateNearby(loc, data);
    }

    public void removeClientBlock(Location loc){
        Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, 0);

        if (!contains(blockLoc))
            return;

        removeClientBlockInternal(blockLoc);
    }

    public void removeClientBlock(Location loc, BlockData data){
        if (!contains(loc, data))
            return;

        removeClientBlockInternal(loc);
    }

    protected void removeClientBlockInternal(Location loc){
        blockMap.remove(loc);
        
        sendBlockUpdateNearby(loc, loc.getBlock().getBlockData());
    }

    public boolean contains(Location loc){
        return hasLocation(loc);
    }

    public boolean contains(Location loc, BlockData data){
        return hasLocation(loc) && blockMap.get(loc).equals(data);
    }

    public BlockData getClientBlock(Location loc) {
        if (hasLocation(loc))
            return blockMap.get(loc);

        return null;
    }

    protected boolean hasLocation(Location loc){
        return blockMap.containsKey(loc);
    }

    protected Map<Location, BlockData> getBlockListInChunk(org.bukkit.World world, int chunkX, int chunkZ){
        Map<Location, BlockData> map = new HashMap<>();

        for (Location loc : new ArrayList<>(blockMap.keySet())) {
            int locChunkX = loc.getBlockX() << 4;
            int locChunkZ = loc.getBlockZ() << 4;
            if (loc.getWorld() != null && loc.getWorld().equals(world) && locChunkX == chunkX && locChunkZ == chunkZ) {
                map.put(loc, blockMap.get(loc));
            }
        }

        return map;
    }

    protected void sendBlockUpdateNearby(Location loc, BlockData data) {
        ConnectionUtil.sendPacketNearby(loc, PacketUtil.getBlockUpdatePacket(loc, data));
    }

    protected void sendMultiBlockUpdateNearby(org.bukkit.World world, int chunkX, int chunkZ, Map<Location, BlockData> dataMap) {
        ConnectionUtil.sendPacketNearby(new Location(world, chunkX * 16, 128, chunkZ * 16), PacketUtil.getMultiBlockUpdatePacket(world, chunkX, chunkZ, dataMap));
    }

    protected void sendBlockUpdate(Player p, Location loc, BlockData data) {
        ConnectionUtil.sendPacket(p, PacketUtil.getBlockUpdatePacket(loc, data));
    }

    protected void sendMultiBlockUpdate(Player p, int chunkX, int chunkZ, Map<Location, BlockData> dataMap) {
        ConnectionUtil.sendPacket(p, PacketUtil.getMultiBlockUpdatePacket(p.getWorld(), chunkX, chunkZ, dataMap));
    }

    @EventHandler
    public void onPlayerChunkLoad(AsyncPlayerLoadChunkEvent e) {
        Map<Location, BlockData> dataMap = getBlockListInChunk(e.getWorld(), e.getChunkX(), e.getChunkZ());

        int size = dataMap.size();

        if (size < 1)
            return;

        if (size == 1) {
            for (Location loc : dataMap.keySet()) {
                sendBlockUpdate(e.getPlayer(), loc, dataMap.get(loc));

                break;
            }
        }
        else {
            sendMultiBlockUpdate(e.getPlayer(), e.getChunkX(), e.getChunkZ(), dataMap);
        }
    }

    @EventHandler
    public void onPlayerBlockUpdate(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutBlockChange) {
            PacketPlayOutBlockChange packet = (PacketPlayOutBlockChange) e.getPacket();

            BlockPosition position = positionField.get(packet);
            Location loc = new Location(e.getTarget().getWorld(), position.getX(), position.getY(), position.getZ(), 0, 0);

            BlockData data = getClientBlock(loc);

            if (data != null) {
                e.setPacket(PacketUtil.getBlockUpdatePacket(loc, data));
            }
        }
    }
}
