package com.storycraft.core.entity;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.BlockIdUtil;
import net.minecraft.server.v1_13_R1.BlockRedstoneWire;
import net.minecraft.server.v1_13_R1.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_13_R1.block.data.CraftBlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.MaterialData;

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

            Material bloodMaterial = Material.REDSTONE_WIRE;
            BlockData data = bloodMaterial.createBlockData("power=" + Math.round((leftHealth / maxHealth) * 15));

            entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, data);
        }
    }

}