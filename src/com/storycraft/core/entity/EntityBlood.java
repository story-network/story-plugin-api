package com.storycraft.core.entity;

import com.storycraft.core.MiniPlugin;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityBlood extends MiniPlugin implements Listener {

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if (e.isCancelled())
            return;

        if (e.getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) e.getEntity();

            double finalDamage = e.getFinalDamage();
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double leftHealth = Math.max(entity.getHealth() - finalDamage, 0);

            entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, (int) (Material.REDSTONE_WIRE.getId() | Math.round((leftHealth / maxHealth) * 15) << 12));
        }
    }

}