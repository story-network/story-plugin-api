package com.storycraft.core.player.head;

import com.storycraft.MiniPlugin;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CustomHat extends MiniPlugin implements Listener {

    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHelmetUpdate(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && e.getWhoClicked().hasPermission("server.player.customhat") && !e.isCancelled() && e.getWhoClicked().getInventory().equals(e.getClickedInventory())
         && e.getWhoClicked().getGameMode() != GameMode.CREATIVE && e.getSlot() == e.getWhoClicked().getInventory().getSize() - 2) {
            ItemStack item = e.getCursor();
            
            e.setCursor(e.getCurrentItem());

            e.getWhoClicked().getInventory().setHelmet(item);

            e.setCancelled(true);
        }
    }
}
