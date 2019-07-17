package com.storycraft.core.morph.entity;

import java.util.Optional;

import com.storycraft.server.entity.metadata.PatchedDataWatcher;
import com.storycraft.util.reflect.Reflect;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;

public class NamedMorphEntity implements IMorphEntity {

	private static Reflect.WrappedField<DataWatcherObject<Boolean>, Entity> customNameVisibleObject;
	private static Reflect.WrappedField<DataWatcherObject<Optional<IChatBaseComponent>>, Entity> customNameObject;
	
	static {
		customNameVisibleObject = Reflect.getField(Entity.class, "aA");
		customNameObject = Reflect.getField(Entity.class, "az");
	}

    private IMorphEntity entity;

    private PatchedDataWatcher watcher;

    public NamedMorphEntity(IMorphEntity entity, boolean customNameVisible, String customName) {
        this.entity = entity;
		this.watcher = new PatchedDataWatcher(entity.getFixedMetadata());
		
		watcher.addPatch(customNameVisibleObject.get(null), customNameVisible);
		watcher.addPatch(customNameObject.get(null), Optional.ofNullable(new ChatComponentText(customName)));
    }

	@Override
	public Entity getNMSEntity() {
		return entity.getNMSEntity();
	}

	@Override
	public DataWatcher getFixedMetadata() {
		return watcher;
	}

	@Override
	public boolean onSpawnSend(Player p, int eid) {
		return entity.onSpawnSend(p, eid);
	}

	@Override
	public boolean onMetadataSend(Player p) {
		return entity.onMetadataSend(p);
	}

	@Override
	public boolean onMoveSend(Player p, short deltaX, short deltaY, short deltaZ, boolean onGround) {
		return entity.onMoveSend(p, deltaX, deltaY, deltaZ, onGround);
	}

	@Override
	public boolean onLookSend(Player p, byte yawAngle, byte pitchAngle, boolean onGround) {
		return entity.onLookSend(p, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onLookAndMove(Player p, short deltaX, short deltaY, short deltaZ, byte yawAngle, byte pitchAngle,
			boolean onGround) {
		return entity.onLookAndMove(p, deltaX, deltaY, deltaZ, yawAngle, pitchAngle, onGround);
	}

	@Override
	public boolean onTeleportSend(Player p, double locX, double locY, double locZ, float yaw, float pitch,
			boolean onGround) {
		return entity.onTeleportSend(p, locX, locY, locZ, yaw, pitch, onGround);
	}

	@Override
	public int[] onDestroySend(Player p, int[] eidList) {
		return entity.onDestroySend(p, eidList);
	}
}