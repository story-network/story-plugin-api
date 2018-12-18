package com.storycraft.test;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.storycraft.StoryPlugin;
import com.storycraft.server.entity.CustomPlayerInfo;
import com.storycraft.server.entity.override.IPlayerOverrideProfileHandler;
import com.storycraft.server.event.server.ServerUpdateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EntityZombie;
import net.minecraft.server.v1_13_R2.World;

public class TestFunction implements Listener {

    private StoryPlugin plugin;

    public TestFunction(StoryPlugin plugin){
        this.plugin = plugin;
    }

    public static void test(StoryPlugin plugin, org.bukkit.World world) {
        plugin.getServer().getPluginManager().registerEvents(new TestFunction(plugin), plugin);

        try {
            plugin.getServerManager().getRegistryManager().getEntityRegistry().add(220, "player_zombie",
                    new CustomPlayerInfo("player_zombie", TestZombiePlayer.class, TestZombiePlayer::new,
                            new ZombieProfileHandler()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onUpdate(ServerUpdateEvent e) {

    }

    public static class TestZombiePlayer extends EntityZombie {
        public TestZombiePlayer(World w) {
            super(w);
        }
    }

    public static class ZombieProfileHandler implements IPlayerOverrideProfileHandler {

        private String texture;
        private String signature;

        public ZombieProfileHandler() {
            this.texture = "eyJ0aW1lc3RhbXAiOjE1NDUwNzk5MzYwMjMsInByb2ZpbGVJZCI6IjU2ZTQyNDMwODg2ZjQxMTk5YjRjNWNkZWI2YTc0YjdmIiwicHJvZmlsZU5hbWUiOiJzcGVjdHJlcGhvZW5peCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRlNWIwZWI0M2NkOWJmMzNjN2MxYTU1NjQwYWNmMDFjOGU1MGFiNjI0NzdiZDhiN2ZlZmUyMzg1M2JjMDFjIn19fQ==";
            this.signature = "fxKXCWjFV6egrM7pYvvmlpwVD8Wor9hrrJLeHg/J2PQ+UOhdUKJ0wMMoAmAQAZzgs4d2URpk3xZkIfrwlFbS0jEyMJcNa/ewcdFhvNsyqZpUosHn4NDCOL69WzJ1UatzKXeS7jElyn9PiFMKNcLrO7jOr9cQkFl0YIEsexQd/5ZPH/S8m+K7lUBexoUOkVyzqEKr2Hlu6ntl6v7n34B+SXGUF4WRcVuznUjSzwtDdqPn2rpfgvRnCygLLS8Grx/erTMLWiJWK21xi2gA9x7pjNBsabn7sOlRcIYZ+F4Y6/mqCRP0ueZ1GUUoiHdciL1osI6pdnkwUy0Db5ONvK2qwboNhj38zcgj4YLZu4B6bZAxu6yjyAhUdoulfiIwtZxWov1y+mkxjnrejhYtf/RNxH3FVAN0GeZeFCsvmC9wQgiocHVZhOMDjhqBeklpc8KwRVHDPnMFL6y8xEy1BOROvcOoNDFBRDzFCl8lZj9TBRX2lcbbjVM45sTHGAXVJ+HVPXDkqrVNTOCDjkLM+8TjiwxEtS0YrFdRThXzoHOBmNLXufFtykKvLFdePjYS0/rYTISAJg9VA+NC9hM3c6MXyl3lPCqNbigN6jh2Zsj2Qbp/weKCOVfD2rB65xqTTcki79KGhJ7dzj1asvFf3UJwiVK/Amw186IJStbMHsjHUfw=";

        }

        @Override
        public GameProfile getProfile(Entity entity) {
            GameProfile profile = new GameProfile(entity.getUniqueID(), "InfectedPlayer");

            profile.getProperties().put("textures", new Property("textures", texture, signature));

			return profile;
		}

    }

}