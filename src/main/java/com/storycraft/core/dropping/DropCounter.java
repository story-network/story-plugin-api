package com.storycraft.core.dropping;

import com.storycraft.MiniPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class DropCounter extends MiniPlugin implements Listener {
    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent e){
        Entity entity = e.getEntity();

        if (entity == null)
            return;

        updateItem((Item) entity);
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent e){
        Entity entity = e.getTarget();

        if (entity == null)
            return;

        //run after merge
        getPlugin().getServer().getScheduler().runTask(getPlugin(), new Runnable() {
            @Override
            public void run() {
                updateItem((Item) entity);
            }
        });
    }

    protected void updateItem(Item item){
        ItemStack itemStack = item.getItemStack();
        String name = "";

        if (itemStack == null)
            return;

        if (itemStack.getItemMeta().getDisplayName() != null)
            name += itemStack.getItemMeta().getDisplayName() + " ";

        if (itemStack.getAmount() > 1) {
            name += ChatColor.GOLD + "x" + itemStack.getAmount();
            item.setCustomNameVisible(true);
            item.setCustomName(name);
        }
    }
}
