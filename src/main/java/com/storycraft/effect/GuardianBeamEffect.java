package com.storycraft.effect;

import java.util.ArrayList;
import java.util.List;

import com.storycraft.MainPlugin;
import com.storycraft.server.entity.metadata.PatchedDataWatcher;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.EntityUtil;
import com.storycraft.util.NMSUtil;
import com.storycraft.util.PacketUtil;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.EntityAreaEffectCloud;
import net.minecraft.server.v1_14_R1.EntityGuardian;
import net.minecraft.server.v1_14_R1.EntityPose;
import net.minecraft.server.v1_14_R1.EntitySquid;
import net.minecraft.server.v1_14_R1.Packet;

public class GuardianBeamEffect extends WorldEffect implements IHasDuration {

    private static Reflect.WrappedMethod<Void, EntityGuardian> guardianSetTargetMethod;
    private static Reflect.WrappedField<DataWatcherObject<EntityPose>, net.minecraft.server.v1_14_R1.Entity> poseFlagObject;

    static {
        guardianSetTargetMethod = Reflect.getMethod(EntityGuardian.class, "a", int.class);
        poseFlagObject = Reflect.getField(net.minecraft.server.v1_14_R1.Entity.class, "POSE");
    }

    private MainPlugin plugin;

    private boolean elderBeam;

    private Location guardianLoc;
    private Location targetLoc;

    private Entity guardianBinded;
    private Entity target;

    private EntityGuardian guardian;
    private int virtualTargetId;

    public GuardianBeamEffect(MainPlugin plugin, Location guardianLoc, Location targetLoc) {
        this(plugin);

        this.guardianLoc = guardianLoc;
        this.targetLoc = targetLoc;
    }

    public GuardianBeamEffect(MainPlugin plugin, Location guardianLoc, Location targetLoc, boolean elderBeam) {
        this(plugin, guardianLoc, targetLoc, null, null, elderBeam);
    }

    public GuardianBeamEffect(MainPlugin plugin, Location guardianLoc, Location targetLoc, Entity guardianBinded, Entity target, boolean elderBeam) {
        this(plugin, elderBeam);

        this.guardianLoc = guardianLoc;
        this.targetLoc = targetLoc;

        this.guardianBinded = guardianBinded;
        this.target = target;
    }

    protected GuardianBeamEffect(MainPlugin plugin) {
        this(plugin, false);
    }

    protected GuardianBeamEffect(MainPlugin plugin, boolean elderBeam) {
        this.plugin = plugin;
        this.elderBeam = elderBeam;
    }

    public void setElderBeam(boolean elderBeam) {
        this.elderBeam = elderBeam;
    }

    public boolean isElderBeam() {
        return elderBeam;
    }

    public Location getGuardianLoc() {
        return guardianLoc;
    }

    public Location getTargetLoc() {
        return targetLoc;
    }

    public Entity getGuardianBinded() {
        return guardianBinded;
    }

    public Entity getTarget() {
        return target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public boolean hasBinder() {
        return guardianBinded != null;
    }

    public boolean validate() {
        return getGuardianLocation().getWorld().equals(getSquidLocation().getWorld());
    }

    @Override
    public void stop() {
        if (!isPlaying())
            return;

        NMSUtil.getNMSEntity(getGuardianBinded()).passengers.remove(guardian);

        for (Player p : getPlayers()) {
            ConnectionUtil.sendPacket(p, PacketUtil.getEntityMountPacket(NMSUtil.getNMSEntity(getGuardianBinded())), PacketUtil.getEntityDestroyPacket(guardian));
        }

        if (!hasTarget()) {
            for (Player p : getPlayers()) {
                ConnectionUtil.sendPacket(p, PacketUtil.getEntityDestroyPacket(virtualTargetId));
            }
        }

        guardian = null;

        super.stop();
    }

    protected Location getSquidLocation() {
        if (hasTarget())
            return target.getLocation();

        return targetLoc;
    }

    protected Location getGuardianLocation() {
        if (hasBinder())
            return guardianBinded.getLocation();

        return guardianLoc;
    }

    protected EntityGuardian createGuardian(World w) {
        EntityGuardian guardian;
        if (isElderBeam()) {
            guardian = EntityUtil.createNMSEntity(w, EntityType.ELDER_GUARDIAN);
        } else {
            guardian = EntityUtil.createNMSEntity(w, EntityType.GUARDIAN);
        }

        guardian.setNoAI(true);
        guardian.setSilent(true);

        PatchedDataWatcher datawatcher = new PatchedDataWatcher(guardian.getDataWatcher());

        //TODO: WARNING | Are there any other good idea without using pose hack?
        datawatcher.addPatch(poseFlagObject.get(null).a(), EntityPose.SLEEPING);

        datawatcher.bindToEntity();

        return guardian;
    }

    @Override
    public long getDuration() {
        return elderBeam ? 2500 : 4000;
    }

    @Override
    public void play(Player... players) {
        if (isPlaying()) {
            stop();
            play(players);
            return;
        }

        if (!validate()) {
            return;
        }

        playInternal(players);

        super.play(players);
    }

    protected void playInternal(Player... playerList) {
        EntityGuardian guardian = createGuardian(getWorld());
        this.guardian = guardian;

        List<Packet> packetList = new ArrayList<>();

        if (hasTarget()) {
            guardianSetTargetMethod.invoke(guardian, getTarget().getEntityId());
        } else {
            EntityAreaEffectCloud cloud = EntityUtil.createNMSEntity(getWorld(), EntityType.AREA_EFFECT_CLOUD);
            cloud.setInvisible(true);
            cloud.setSilent(true);
            cloud.setRadius(0f);

            this.virtualTargetId = cloud.getId();

            Location virtualTargetLoc = getSquidLocation();

            cloud.setLocation(virtualTargetLoc.getX(), virtualTargetLoc.getY(), virtualTargetLoc.getZ(), virtualTargetLoc.getYaw(), virtualTargetLoc.getPitch());

            guardianSetTargetMethod.invoke(guardian, virtualTargetId);
            packetList.add(PacketUtil.getEntitySpawnPacket(cloud));
        }

        Location loc = getGuardianLocation();

        guardian.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        guardian.setInvisible(true);
        guardian.setNoGravity(true);
        guardian.setNoAI(true);

        packetList.add(PacketUtil.getEntitySpawnPacket(guardian));

        if (hasBinder()) {
            NMSUtil.getNMSEntity(getGuardianBinded()).passengers.add(guardian);

            packetList.add(PacketUtil.getEntityMountPacket(NMSUtil.getNMSEntity(getGuardianBinded())));
        }

        Packet[] packets = packetList.toArray(new Packet[packetList.size()]);

        for (Player p : playerList) {
            ConnectionUtil.sendPacket(p, packets);
        }
    }

    @Override
    public World getWorld() {
        return getGuardianLocation().getWorld();
    }

}