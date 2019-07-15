package com.storycraft.core.player.movement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.storycraft.MiniPlugin;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class CrawlToggler extends MiniPlugin implements Listener {

    private static final int DOUBLE_SNEAK_MAX_DELAY = 800;

    private Map<UUID, Long> sneakTimeMap;

    public CrawlToggler() {
        this.sneakTimeMap = new HashMap<>();
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        getPlugin().getEntityManager().getCrawlMovement().setCrawling(e.getPlayer(), false);
    }

    @EventHandler
    public void toggleCrawling(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            if (sneakTimeMap.containsKey(e.getPlayer().getUniqueId())) {
                if (System.currentTimeMillis() - sneakTimeMap.remove(e.getPlayer().getUniqueId()) <= DOUBLE_SNEAK_MAX_DELAY && e.getPlayer().isOnGround()) {
                    getPlugin().getEntityManager().getCrawlMovement().setCrawling(e.getPlayer(), true);

                    return;
                }
            }

            sneakTimeMap.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (sneakTimeMap.containsKey(e.getPlayer().getUniqueId())) {
            sneakTimeMap.remove(e.getPlayer().getUniqueId());
        }
    }

}