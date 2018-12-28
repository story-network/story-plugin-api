package com.storycraft.core.punish;

import java.util.UUID;

import org.bukkit.event.Listener;

public interface IPunishment {

    public PunishmentHandler createPunishmentHandler(UUID id);
    
    public static class PunishmentHandler implements Listener {

        private UUID target;

        protected PunishmentHandler(UUID target) {
            this.target = target;
        }

        public UUID getTarget() {
            return target;
        }
    }
}