package com.storycraft.core.punish.punishment;

import java.util.UUID;

import com.storycraft.core.punish.IPunishment;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezePunishment implements IPunishment {

    @Override
    public PunishmentHandler createPunishmentHandler(UUID id) {
		return new FreezeHandler(id);
    }
    
    public class FreezeHandler extends IPunishment.PunishmentHandler {

        public FreezeHandler(UUID id) {
            super(id);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (e.getPlayer() != null && e.getPlayer().getUniqueId().equals(getTarget())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getPlayer() != null && e.getPlayer().getUniqueId().equals(getTarget())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent e) {
            if (e.getPlayer() != null && e.getPlayer().getUniqueId().equals(getTarget())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onBreak(BlockDamageEvent e) {
            if (e.getPlayer() != null && e.getPlayer().getUniqueId().equals(getTarget())) {
                e.setCancelled(true);
            }
        }

    }

}