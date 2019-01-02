package com.storycraft.core.disguise;

import com.storycraft.core.MiniPlugin;
import com.storycraft.core.morph.MorphManager;
import com.storycraft.core.morph.NamedMorphInfoWrapper;
import com.storycraft.core.morph.SimpleBlockMorphInfo;
import com.storycraft.core.morph.SimpleMorphInfo;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HeadDisguise extends MiniPlugin implements Listener {

    public MorphManager getMorphManager() {
        return getPlugin().getDecorator().getMorphManager();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            updatePlayerMorph(p);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        updatePlayerMorph(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        getMorphManager().removeMorph(e.getPlayer());
    }

    @EventHandler
    public void onPlayerHelmetUpdate(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && !e.isCancelled() && e.getWhoClicked().getInventory().equals(e.getClickedInventory())
         && e.getSlot() == e.getWhoClicked().getInventory().getSize() - 2/*Helmet*/) {
            if (updatePlayerMorph((Player) e.getWhoClicked(), e.getCursor())) {
                ItemStack item = e.getCursor();
                e.setCursor(e.getCurrentItem());
                e.setCurrentItem(item);

                e.setCancelled(true);

                e.getWhoClicked().sendMessage(MessageUtil.getPluginMessage(MessageType.ALERT, "HeadDisguise", "머리 아이템이 장착된 상태입니다. 다른 플레이어에게는 해당 몹으로 보이게 됩니다"));
            }
        }
    }

    protected boolean updatePlayerMorph(Player p) {
        return updatePlayerMorph(p, p.getInventory().getHelmet());
    }

    protected boolean updatePlayerMorph(Player p, ItemStack item) {
        if (item != null) {
            EntityType type = getHeadType(item);

            if (type != null && p.hasPermission("server.headdisguise." + type.getName())) {

                if (type == EntityType.SHULKER) {
                    getMorphManager().setMorph(new NamedMorphInfoWrapper(new SimpleBlockMorphInfo(p, Material.PURPLE_SHULKER_BOX.createBlockData())));
                }
                else {
                    getMorphManager().setMorph(new NamedMorphInfoWrapper(new SimpleMorphInfo(p, type)));
                }

                return true;
            }
            else {
                getMorphManager().removeMorph(p);
            }
        }
        else {
            getMorphManager().removeMorph(p);
        }

        return false;
    }

    public EntityType getHeadType(ItemStack item) {
        switch (item.getType()) {
            case CREEPER_HEAD:
                return EntityType.CREEPER;

            case SKELETON_SKULL:
                return EntityType.SKELETON;

            case WITHER_SKELETON_SKULL:
                return EntityType.WITHER_SKELETON;

            case ZOMBIE_HEAD:
                return EntityType.ZOMBIE;

            case TURTLE_HELMET:
                return EntityType.TURTLE;

            case SHULKER_SHELL:
                return EntityType.SHULKER;

            default:
                return null;
        }
    }
}