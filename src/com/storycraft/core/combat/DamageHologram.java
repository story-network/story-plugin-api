package com.storycraft.core.combat;

import com.storycraft.core.MiniPlugin;
import com.storycraft.server.clientside.ClientEntityManager;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHologram extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if (e.getEntity() == null)
            return;
        
        ClientEntityManager manager = getPlugin().getServerManager().getClientManager().getClientEntityManager();

        Entity hologram = createDamageHologram(e.getEntity(), e.getFinalDamage());

        manager.addClientEntity(hologram);
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                manager.removeClientEntity(hologram);
            }
        }, 30);
    }

    private Entity createDamageHologram(org.bukkit.entity.Entity e, double finalDamage) {
        World world = ((CraftWorld)e.getWorld()).getHandle();

        Location loc = e.getLocation();

        EntityAreaEffectCloud stand = new EntityAreaEffectCloud(world);
        stand.setRadius(0f);
        stand.setInvisible(true);
        stand.setCustomNameVisible(true);
        stand.setPosition(loc.getX() + Math.random() - 0.5d, loc.getY() + Math.random() - 0.25d, loc.getZ() + Math.random() - 0.5d);
        stand.setCustomName(ChatColor.RED + "" + finalDamage);

        return stand;
    }
}
