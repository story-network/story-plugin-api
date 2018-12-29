package com.storycraft.core.explosion;

import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.util.BlockIdUtil;
import com.storycraft.util.Parallel;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    protected final static int RESTORE_DELAY = 5000;
    protected final static int RESTORE_INTERVAL = 50;

    private JsonConfigFile configFile;

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
        plugin.getConfigManager().addConfigFile("explosion.json", configFile = new JsonConfigPrettyFile());
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(getExplosionHandler(), getPlugin());
    }

    protected JsonConfigEntry getWorldEntry(World w) {
        JsonConfigEntry entry = configFile.getObject(w.getName());

        if (entry == null)
            configFile.set(w.getName(), entry = configFile.createEntry());

        return entry;
    }

    public ExplosionType getExplosionType(World w) {
        try {
            JsonConfigEntry entry = getWorldEntry(w);

            return ExplosionType.valueOf(entry.get("type").getAsString());
        } catch (Exception e) {
            setExplosionType(w, ExplosionType.DEFAULT);

            return ExplosionType.DEFAULT;
        }
    }

    public void setExplosionType(World w, ExplosionType type) {
        JsonConfigEntry entry = getWorldEntry(w);

        entry.set("type", type.toString());
    } 

    public class ExplosionHandler implements Listener {
    
        private final static String EXPLOSION_BLOCK_FLAG = "isExplosionBlock";
    
        private Explosion explosion;
    
        public ExplosionHandler(Explosion explosion){
            this.explosion = explosion;
        }
    
        protected Explosion getExplosion() {
            return explosion;
        }
    
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBlockExplode(BlockExplodeEvent e) {
            if (e.isCancelled())
                return;

            ExplosionType type = getExplosionType(e.getBlock().getWorld());

            if (e.getBlock() != null && type == ExplosionType.IGNORE) {
                e.setCancelled(true);
    
                return;
            }
            else if (type == ExplosionType.ONLY_DAMAGE) {
                e.blockList().clear();

                return;
            }
    
            onExplosion(e.getBlock().getLocation(), type, e.blockList(), e.getYield());
        }
    
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEntityExplode(EntityExplodeEvent e) {
            if (e.isCancelled())
                return;

            ExplosionType type = getExplosionType(e.getLocation().getWorld());

            if (type == ExplosionType.IGNORE) {
                e.setCancelled(true);
        
                return;
            }
            else if (type == ExplosionType.ONLY_DAMAGE) {
                e.blockList().clear();

                return;
            }
    
            onExplosion(e.getLocation(), type, e.blockList(), e.getYield());
    
            //simulate small explosion effect
            if (e.getEntity() instanceof Projectile) {
                e.setCancelled(true);
    
                for (Block b : e.blockList()){
                    b.breakNaturally();
                }
    
                e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0);
            }
        }
    
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onExplosionPrime(ExplosionPrimeEvent e){
            if (e.getEntity() != null && getExplosionType(e.getEntity().getWorld()) == ExplosionType.IGNORE) {
                e.setCancelled(true);

                return;
            }

            if (!e.isCancelled() && e.getEntity() instanceof Fireball){
                Fireball fireball = (Fireball) e.getEntity();
    
                if (fireball.getShooter() instanceof Ghast || fireball.getShooter() instanceof Wither)
                    e.setRadius(Math.max(e.getRadius(), Explosion.MOB_FIREBALL_POWER));
            }
        }
    
        public void onExplosion(Location center, ExplosionType type, List<Block> blockList, float yield) {
            if (blockList.size() == 0)
                return;
    
            double limit = (Math.log(blockList.size()) / Math.pow(blockList.size(), 0.61275));
    
            Random rnd = new Random();
            
            World w = center.getWorld();

            int sizeSqrt = (int) Math.sqrt(blockList.size());
    
            Function<Block, Void> handle = b -> {
                if (b == null || b.getType() == Material.AIR || isExplosive(b.getType()))
                    return null;
    
                if (rnd.nextDouble() <= limit){
                    Vector vec = b.getLocation().toVector().subtract(center.toVector());
    
                    if (vec.getY() < 0)
                        vec.setY(vec.getY() * -1);
    
                    BlockData data = b.getBlockData();
    
                    getExplosion().runSync(() -> {
                        FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), data);
    
                        fb.setHurtEntities(true);
                        fb.setDropItem(false);
    
                        fb.setVelocity(vec.normalize());
    
                        setExplosionBlock(fb, true);

                        if (type == ExplosionType.FLYING_BLOCKS_RESTORE_RANDOM) {
                            Location loc = b.getLocation();

                            Runnable task = () -> {
                                if (loc.getBlock().getType() == Material.AIR) {
                                    w.playEffect(loc.getBlock().getLocation(), Effect.STEP_SOUND, BlockIdUtil.getCombinedId(data));
                                    loc.getBlock().setBlockData(data);
                                }
                            };

                            getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), task, (int) (RESTORE_DELAY + rnd.nextDouble() * sizeSqrt * RESTORE_INTERVAL));
                        }
    
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
                    public void run(Block b) {
                        handle.apply(b);
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
                ExplosionType type = getExplosionType(e.getEntity().getWorld());

                if (type == ExplosionType.FLYING_BLOCKS_CLEAR || type == ExplosionType.FLYING_BLOCKS_RESTORE_RANDOM) {
                    e.setCancelled(true);
    
                    FallingBlock fallingBlock = (FallingBlock) e.getEntity();
    
                    //Play Block Breaking Effect
                    e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, BlockIdUtil.getCombinedId(fallingBlock.getBlockData()));
                }
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
}