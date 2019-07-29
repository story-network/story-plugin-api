package com.storycraft.server.event.client;

import com.storycraft.server.ServerExtension;
import com.storycraft.server.packet.AsyncPacketInEvent;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_14_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_14_R1.PacketPlayInItemName;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutUnloadChunk;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class ClientEventManager extends ServerExtension implements Listener {

    private Reflect.WrappedField<Integer, PacketPlayOutMapChunk> chunkLoadPacketX;
    private Reflect.WrappedField<Integer, PacketPlayOutMapChunk> chunkLoadPacketZ;
    private Reflect.WrappedField<Boolean, PacketPlayOutMapChunk> isFullChunkField;

    private Reflect.WrappedField<Integer, PacketPlayOutUnloadChunk> chunkUnloadPacketX;
    private Reflect.WrappedField<Integer, PacketPlayOutUnloadChunk> chunkUnloadPacketZ;

    @Override
    public void onEnable(){
        this.chunkLoadPacketX = Reflect.getField(PacketPlayOutMapChunk.class, "a");
        this.chunkLoadPacketZ = Reflect.getField(PacketPlayOutMapChunk.class, "b");
        this.isFullChunkField = Reflect.getField(PacketPlayOutMapChunk.class, "g");

        this.chunkUnloadPacketX = Reflect.getField(PacketPlayOutUnloadChunk.class, "a");
        this.chunkUnloadPacketZ = Reflect.getField(PacketPlayOutUnloadChunk.class, "b");

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onPacketOut(AsyncPacketOutEvent e){
        Packet packetOut = e.getPacket();

        if (packetOut instanceof PacketPlayOutUnloadChunk){
            PacketPlayOutUnloadChunk packet = (PacketPlayOutUnloadChunk) packetOut;
            Player p = e.getTarget();

            World w = p.getWorld();

            int locX = chunkUnloadPacketX.get(packet);
            int locZ = chunkUnloadPacketZ.get(packet);

            AsyncPlayerUnloadChunkEvent event = new AsyncPlayerUnloadChunkEvent(p, w, locX, locZ);
            getPlugin().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled())
                e.setCancelled(true);
        } else if (packetOut instanceof PacketPlayOutMapChunk){
            try {
                PacketPlayOutMapChunk packet = (PacketPlayOutMapChunk) packetOut;
                Player p = e.getTarget();
    
                World w = p.getWorld();
    
                int locX = chunkLoadPacketX.get(packet);
                int locZ = chunkLoadPacketZ.get(packet);
    
                boolean isFullChunk = isFullChunkField.get(packet);
    
                AsyncPlayerLoadChunkEvent event = new AsyncPlayerLoadChunkEvent(p, w, locX, locZ, isFullChunk);
                getPlugin().getServer().getPluginManager().callEvent(event);
    
                if (event.isCancelled())
                    e.setCancelled(true);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPacketIn(AsyncPacketInEvent e) {
        Packet packetIn = e.getPacket();

        if (packetIn instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) packetIn;

            BlockPosition pos = packet.b();

            switch (packet.d()) {
                case START_DESTROY_BLOCK:
                    AsyncPlayerDigEvent startEvent = new AsyncPlayerDigStartEvent(e.getSender(), new Location(e.getSender().getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                    
                    getPlugin().getServer().getPluginManager().callEvent(startEvent);

                    e.setCancelled(startEvent.isCancelled());
                    break;
                
                case ABORT_DESTROY_BLOCK:
                    AsyncPlayerDigEvent cancelEvent = new AsyncPlayerDigCancelEvent(e.getSender(), new Location(e.getSender().getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                    
                    getPlugin().getServer().getPluginManager().callEvent(cancelEvent);

                    e.setCancelled(cancelEvent.isCancelled());
                    break;

                case STOP_DESTROY_BLOCK:
                    AsyncPlayerDigEvent doneEvent = new AsyncPlayerDigDoneEvent(e.getSender(), new Location(e.getSender().getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                    
                    getPlugin().getServer().getPluginManager().callEvent(doneEvent);

                    e.setCancelled(doneEvent.isCancelled());
                    break;

                default:
                    return;
            }
        } else if (packetIn instanceof PacketPlayInCustomPayload) {
            PacketPlayInCustomPayload packet = (PacketPlayInCustomPayload) packetIn;

            if (PacketPlayInCustomPayload.a.equals(packet.tag)) {
                String brand = packet.data.readUTF(32767);

                getPlugin().getServer().getPluginManager().callEvent(new AsyncPlayerBrandSentEvent(e.getSender(), brand));
            }
        } else if (packetIn instanceof PacketPlayInItemName) {
            PacketPlayInItemName packet = (PacketPlayInItemName) packetIn;

            AsyncAnvilNameEvent event = new AsyncAnvilNameEvent(e.getSender(), packet.b());

            getPlugin().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }
}
