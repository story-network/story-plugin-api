package com.storycraft.core.explosion;

public enum ExplosionType {

    FLYING_BLOCKS,
    FLYING_BLOCKS_CLEAR,
    FLYING_BLOCKS_RESTORE_RANDOM,
    IGNORE,
    ONLY_DAMAGE;

    public static final ExplosionType DEFAULT = FLYING_BLOCKS_CLEAR;
}