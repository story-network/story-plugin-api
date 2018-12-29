package com.storycraft.core.punish;

import java.util.UUID;

public class PunishmentInfo {

    private IPunishment type;
    private long expireAt;

    public PunishmentInfo(IPunishment type) {
        this(type, -1);
    }

    public PunishmentInfo(IPunishment type, long expireAt) {
        this.type = type;
        this.expireAt = expireAt;
    }

    public IPunishment getType() {
        return type;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public boolean isExpired(long time) {
        return getExpireAt() <= time;
    }
}