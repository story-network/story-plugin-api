package com.storycraft.core.morph;

import java.util.UUID;

import com.storycraft.core.morph.entity.IMorphEntity;

import org.bukkit.entity.Entity;

import net.minecraft.server.v1_13_R2.ChatComponentText;

public class MorphInfo {

    private Entity entity;
    private IMorphEntity morph;

    public MorphInfo(Entity entity, IMorphEntity morph) {
        this.entity = entity;
        this.morph = morph;
    }

    public Entity getEntity() {
        return entity;
    }

    public IMorphEntity getMorph() {
        return morph;
    }

}