package com.storycraft.server.entity;

import org.bukkit.entity.Entity;

public class EntityPacketListener extends EntityPacketListenerAbstract {

    private Entity entity;
    private IEntityHandler handler;

    public EntityPacketListener(Entity entity, IEntityHandler handler) {
        this.entity = entity;
        this.handler = handler;
    } 

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public IEntityHandler getHandler() {
		return handler;
	}
}