package com.storycraft.core.entity;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.BlockIdUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftAnaloguePowerable;
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

            if (finalDamage < 0)
                return;

            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double leftHealth = Math.max(entity.getHealth() - finalDamage, 0);

            Material bloodMaterial = Material.REDSTONE_WIRE;
            BlockData data = getPlugin().getServer().createBlockData(bloodMaterial, "[" + "power" + "=" + ((int) Math.floor(Math.max((leftHealth / maxHealth), 1) * 15)) + "]");

            entity.getWorld().playEffect(entity.getEyeLocation(), Effect.STEP_SOUND, BlockIdUtil.getCombinedId(data));
        }
    }

}