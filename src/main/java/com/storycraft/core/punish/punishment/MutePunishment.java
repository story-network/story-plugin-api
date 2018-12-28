package com.storycraft.core.punish.punishment;

import java.util.UUID;

import com.storycraft.core.punish.IPunishment;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MutePunishment implements IPunishment {

    @Override
    public PunishmentHandler createPunishmentHandler(UUID id) {
		return new MutePunishmentHandler(id);
    }
    
    public class MutePunishmentHandler extends PunishmentHandler {

        public MutePunishmentHandler(UUID id) {
            super(id);
        }

        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            if (getTarget().equals(e.getPlayer().getUniqueId()))
                e.setCancelled(true);
        }
    }

}