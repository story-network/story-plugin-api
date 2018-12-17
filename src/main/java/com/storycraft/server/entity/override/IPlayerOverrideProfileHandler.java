package com.storycraft.server.entity.override;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_13_R2.Entity;

public interface IPlayerOverrideProfileHandler<T extends Entity> {

    GameProfile getProfile(T entity);

}