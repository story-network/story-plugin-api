package com.storycraft.test;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.storycraft.StoryPlugin;
import com.storycraft.core.morph.SimpleBlockMorphInfo;
import com.storycraft.effect.GuardianBeamEffect;
import com.storycraft.effect.IHasDuration;
import com.storycraft.effect.WorldEffect;
import com.storycraft.effect.player.EffectTracker;
import com.storycraft.server.entity.CustomPlayerInfo;
import com.storycraft.server.entity.metadata.PatchedDataWatcher;
import com.storycraft.server.entity.override.IPlayerOverrideProfileHandler;
import com.storycraft.server.event.client.AsyncPlayerDigCancelEvent;
import com.storycraft.server.event.client.AsyncPlayerDigStartEvent;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.Blocks;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityMonster;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityPose;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EntityZombie;
import net.minecraft.server.v1_14_R1.IRangedEntity;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_14_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalDoorOpen;
import net.minecraft.server.v1_14_R1.PathfinderGoalEatTile;
import net.minecraft.server.v1_14_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_14_R1.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_14_R1.World;

public class TestFunction implements Listener {

    private static Reflect.WrappedField<DataWatcherObject<EntityPose>, Entity> glideFlagObject;

    static {
        glideFlagObject = Reflect.getField(Entity.class, "POSE");
    }

    private StoryPlugin plugin;

    public TestFunction(StoryPlugin plugin){
        this.plugin = plugin;

        this.test();
    }

    public void test() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        try {
            plugin.getServerManager().getRegistryManager().getEntityRegistry().add(256, new CustomPlayerInfo<TestZombiePlayer>("player_zombie", TestZombiePlayer::new, new ZombieProfileHandler()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        //plugin.getDecorator().getMorphManager().setMorph(new SimpleBlockMorphInfo(e.getEntity(), Material.DIRT.createBlockData()));

        /*IHasDuration effect = new GuardianBeamEffect(plugin, null, null, e.getDamager(), e.getEntity(), true);

        effect.play();

        new EffectTracker(plugin).setOnEndListener(() -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 10);
            });
        }).track(effect);*/
    }

    public class TestZombiePlayer extends EntityMonster implements IRangedEntity {

        public TestZombiePlayer(EntityTypes<? extends EntityMonster> entitytypes, World w) {
            super((EntityTypes<? extends EntityMonster>) EntityTypes.a("server:player_zombie").get(), w);

            setCustomNameVisible(true);
            setCustomName(new ChatComponentText(":)"));

            this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.1d, 25, 60, 24));
            this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 1.0d, 1));
            this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
            this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 1.0F));
            this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
            this.goalSelector.a(3, new PathfinderGoalDoorOpen(this, true));

            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class, true));
            this.targetSelector.a(1, new PathfinderGoalEatTile(this));

            PatchedDataWatcher datawatcher = new PatchedDataWatcher(super.datawatcher);

            datawatcher.addPatch(glideFlagObject.get(null), EntityPose.SWIMMING);

            datawatcher.bindToEntity();
        }

        @Override
        public boolean isPersistent() {
            return true;
        }

        @Override
        public void a(EntityLiving arg0, float arg1) {
            IHasDuration effect = new GuardianBeamEffect(plugin, null, null, getBukkitEntity(), arg0.getBukkitEntity(), true);

            effect.play();
    
            new EffectTracker(plugin).setOnEndListener(() -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    arg0.damageEntity(DamageSource.mobAttack(this), 5f);
                });
            }).track(effect);
        }

    }

    public static class ZombieProfileHandler implements IPlayerOverrideProfileHandler {

        private String texture;
        private String signature;

        public ZombieProfileHandler() {
            this.texture = "eyJ0aW1lc3RhbXAiOjE1MzE2MjYzMDI5MTEsInByb2ZpbGVJZCI6IjEwMGU2MWE5YzU2ODRkNDg4N2MzMzBhNzg0NTE1NGE5IiwicHJvZmlsZU5hbWUiOiJzdG9yeWNyYWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NWY4MjczMjY5ZTc5NzU5MzRlY2NhZDFiNTZmYzVmYTUxNGM0MWU2NzU3NTk2MDg2NTJlNGNkNWI3Y2FlMzEifX19";
            this.signature = "PWa/+TuZGqQlCwwsDzSX7cqeTxV3Dj9EogTtQEjbvzIu3LoWQhj7Jh04+U4b16jtJgE71VQoBi/Upi0u9QpSLY0QrUsq4Ll2MbM0dFXN1zvTjaTKKxfLjy4fKZRa2wCn1r2xQBjRIUrsMSUOZ5S9uCAWyYDsTHFEf8Yq6/Hx2xd29pnpTEOngzMCFEUK5lYFuVlNN2zWYSqO8czNfmLWG/gVpd1oO0G/8eDlCeXBSfCXAF2VbPy1S30b4IWUAk+I/KpWgSzDcVuMiTpN1GfPNMUbdj1Q88kf8cj3DyRPKZLiib4ALVF7DKILeKIOoAKYYjV+AnBeVfyjREMf1QK/Ily1O026LvbDG2I5bCDQg4HRQXc3G9EN88Hfw/Fy7Mggte6XWHXiN3X+dgx5zygP9svH733ZFIQE7vWatfAxjNfliVv2Uas61JrlpUft/mlDxL4qKFPaYtEmSI6Y8sJhPgtm0WXL8mr9+dCGA/CgIkM5nAeN77jouF0FMuN8EZPWqEOMxdrCeEkoanDyCLR67l0Y9cIJObbAHPcOhdvSoXzw4LxtMUlZpRHPE7BSOQXoK6hv2TydQxeWC04JbKCmniyGVxQfv6ooUZv+Fu/aL2g3TNgQpWFfhEVzOyJZxBLgxbz3n6kNfbfE+hHs9dPIzczfGSwRb9upTMRZa0LKolk=";

        }

        @Override
        public GameProfile getProfile(Entity entity) {
            GameProfile profile = new GameProfile(entity.getUniqueID(), entity.getScoreboardDisplayName().getText());

            profile.getProperties().put("textures", new Property("textures", texture, signature));

			return profile;
		}

    }

}