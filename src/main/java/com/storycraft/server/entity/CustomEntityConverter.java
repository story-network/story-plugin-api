package com.storycraft.server.entity;

import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_13_R1.EntityTypes;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEntityConverter implements Listener {

    private ServerEntityRegistry serverEntityRegistry;
    private Reflect.WrappedField<Integer, PacketPlayOutSpawnEntityLiving> c;

    public CustomEntityConverter(ServerEntityRegistry serverEntityRegistry) {
        this.serverEntityRegistry = serverEntityRegistry;
        this.c = Reflect.getField(PacketPlayOutSpawnEntityLiving.class, "c");
    }

    public ServerEntityRegistry getServerEntityRegistry() {
        return serverEntityRegistry;
    }

    @EventHandler
    public void onLivingPacket(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutSpawnEntityLiving) {
            PacketPlayOutSpawnEntityLiving packet = (PacketPlayOutSpawnEntityLiving) e.getPacket();

            EntityTypes type = serverEntityRegistry.getById(c.get(packet));
            if (serverEntityRegistry.contains(type)) {
                c.set(packet, EntityTypes.REGISTRY.a(type));
            }
        }
    }


}
