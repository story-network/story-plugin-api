package com.storycraft.server.entity.override;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_14_R1.Entity;

public interface IPlayerOverrideProfileHandler {

    GameProfile getProfile(Entity entity);

}