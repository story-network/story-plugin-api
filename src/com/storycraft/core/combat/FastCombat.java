package com.storycraft.core.combat;

import com.storycraft.core.MiniPlugin;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class FastCombat extends MiniPlugin implements Listener {

    @Override
    public void onEnable(){
        for (Player p : getPlugin().getServer().getOnlinePlayers())
            p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(calcModifier(p.getInventory().getItemInMainHand()));

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onPlayerChangeSlot(PlayerItemHeldEvent e){
        ItemStack newItem = e.getPlayer().getInventory().getItem(e.getNewSlot());

        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(calcModifier(newItem));
    }

    @EventHandler
    public void onItemSwapped(PlayerSwapHandItemsEvent e){
        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(calcModifier(e.getMainHandItem()));
    }

    public double calcModifier(ItemStack item){
        double modifier = 15;

        if (item != null) {
            if (item.getType() == Material.WOOD_SWORD
                    || item.getType() == Material.GOLD_SWORD
                    || item.getType() == Material.IRON_SWORD
                    || item.getType() == Material.DIAMOND_SWORD){
                modifier = 10.25;
            }
            else if (item.getType() == Material.WOOD_AXE
                    || item.getType() == Material.GOLD_AXE
                    || item.getType() == Material.IRON_AXE
                    || item.getType() == Material.DIAMOND_AXE){
                modifier = 4.75;
            }
        }

        return modifier;
    }

}
