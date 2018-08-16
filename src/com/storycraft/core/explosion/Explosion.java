package com.storycraft.core.explosion;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.BlockIdUtil;
import com.storycraft.util.Parallel;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Explosion extends MiniPlugin {

    protected final static float LIGHTNING_EXPLOSION = 2.25f;
    protected final static float MOB_FIREBALL_POWER = 3.125f;

    private ExplosionHandler explosionHandler;

    public Explosion() {
        this.explosionHandler = null;
    }

    public ExplosionHandler getExplosionHandler() {
        return explosionHandler;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        this.explosionHandler = new ExplosionHandler(this);
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(getExplosionHandler(), getPlugin());
    }
}

class ExplosionHandler implements Listener {

    private final static String EXPLOSION_BLOCK_FLAG = "isExplosionBlock";

    private Explosion explosion;

    public ExplosionHandler(Explosion explosion){
        this.explosion = explosion;
    }

    protected Explosion getExplosion() {
        return explosion;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (e.isCancelled())
            return;

        onExplosion(e.getBlock().getLocation(), e.blockList(), e.getYield());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.isCancelled())
            return;

        onExplosion(e.getLocation(), e.blockList(), e.getYield());

        //simulate small explosion effect
        if (e.getEntity() instanceof Projectile) {
            e.setCancelled(true);

            for (Block b : e.blockList()){
                b.breakNaturally();
            }

            e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0);
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e){
        if (!e.isCancelled() && e.getEntity() instanceof Fireball){
            Fireball fireball = (Fireball) e.getEntity();

            if (fireball.getShooter() instanceof Ghast || fireball.getShooter() instanceof Wither)
                e.setRadius(Math.max(e.getRadius(), Explosion.MOB_FIREBALL_POWER));
        }
    }

    public void onExplosion(Location center, List<Block> blockList, float yield) {
        if (blockList.size() == 0)
            return;

        double limit = (Math.log(blockList.size()) / Math.pow(blockList.size(), 0.61275));

        Random rnd = new Random();

        Function<Block, Void> handle = b -> {
            if (b == null || b.getType() == Material.AIR || isExplosive(b.getType()))
                return null;

            if (rnd.nextDouble() <= limit){
                Vector vec = b.getLocation().toVector().subtract(center.toVector());

                if (vec.getY() < 0)
                    vec.setY(vec.getY() * -1);

                getExplosion().runSync(() -> {
                    FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());

                    fb.setHurtEntities(true);
                    fb.setDropItem(false);

                    fb.setVelocity(vec.normalize());

                    setExplosionBlock(fb, true);

                    return null;
                });
            }
            return null;
        };

        if (blockList.size() <= 250) {
            for (Block b : blockList) {
                handle.apply(b);
            }
        }
        else {
            Parallel.forEach(blockList, new Parallel.Operation<Block>() {
                @Override
                public void run(Block param) {
                    handle.apply(param);
                }
            });
        }
    }

    public boolean isExplosive(Material material) {
        if (material == null)
            return false;

        return material == Material.TNT;
    }

    public void setExplosionBlock(FallingBlock fallingBlock, boolean flag){
        fallingBlock.setMetadata(EXPLOSION_BLOCK_FLAG, new FixedMetadataValue(getExplosion().getPlugin(), flag));
    }

    public boolean isExplosionBlock(FallingBlock fallingBlock){
        if (!fallingBlock.hasMetadata(EXPLOSION_BLOCK_FLAG))
            return false;

        return fallingBlock.getMetadata(EXPLOSION_BLOCK_FLAG).get(0).asBoolean();
    }

    @EventHandler
    public void onBlockPieceLand(EntityChangeBlockEvent e){
        if (!e.isCancelled() && e.getEntity() instanceof FallingBlock && isExplosionBlock((FallingBlock) e.getEntity())) {
            e.setCancelled(true);

            FallingBlock fallingBlock = (FallingBlock) e.getEntity();

            //Play Block Breaking Effect
            e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, BlockIdUtil.getCombinedId(fallingBlock.getBlockData()));
        }
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e){
        if (e.isCancelled())
            return;

        LightningStrike lightningStrike = e.getLightning();

        lightningStrike.getWorld().createExplosion(lightningStrike.getLocation(), Explosion.LIGHTNING_EXPLOSION);
    }
}