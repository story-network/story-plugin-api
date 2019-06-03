package com.storycraft.core.entity.movement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.storycraft.core.MiniPlugin;

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

public class CrawlMovement extends MiniPlugin implements Listener {

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onCrawling(EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e.getEntity();

            if (isCrawling(living) && !e.isGliding()) {
                if (!living.isOnGround()) {
                    setCrawling(living, false);
                }
                
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void hideOnCrawl(PlayerToggleSneakEvent e) {
        if (isCrawling(e.getPlayer()) && !e.isSneaking()) {
            e.setCancelled(true);
        }
    }

    public boolean isCrawling(LivingEntity e) {
        if (!e.hasMetadata("crawling"))
            return false;
        
        List<MetadataValue> list = e.getMetadata("crawling");

        if (list.size() < 1)
            return false;

        for (MetadataValue value : list) {
            if (getPlugin().equals(value.getOwningPlugin())) {
                return value.value() == Boolean.TRUE;
            }
        }

        return false;
    }
    
    public void setCrawling(LivingEntity e, boolean flag) {
        if (e.hasMetadata("crawling")) {
            if (isCrawling(e) == flag)
                return;
            
            e.removeMetadata("crawling", getPlugin());
        }

        EntityToggleCrawlEvent event = new EntityToggleCrawlEvent(e, flag);
        getPlugin().getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        e.setMetadata("crawling", new FixedMetadataValue(getPlugin(), flag));
        e.setGliding(flag);
        
        if (e instanceof Player) {
            Player p = (Player) e;
            p.setSneaking(flag);
        }
    }

}