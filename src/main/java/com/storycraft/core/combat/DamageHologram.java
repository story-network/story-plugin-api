package com.storycraft.core.combat;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.hologram.Hologram;
import com.storycraft.core.hologram.HologramManager;
import com.storycraft.core.hologram.ShortHologram;
import com.storycraft.server.clientside.ClientEntityManager;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHologram extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof LivingEntity))
            return;

        HologramManager hologramManager = getPlugin().getDecorator().getHologramManager();
        Hologram hologram = new ShortHologram(e.getEntity().getLocation().add(Math.random() - 0.5d, Math.random() - 0.25d, Math.random() - 0.5d), ChatColor.RED + "" + Math.floor(e.getFinalDamage() * 100) / 100);

        hologramManager.addHologram(hologram);
        getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run() {
                hologramManager.removeHologram(hologram);
            }
        }, 25);
    }
}
