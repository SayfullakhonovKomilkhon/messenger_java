package com.messenger.chat.entity;

public enum GroupRole {
    OWNER(3),
    ADMIN(2),
    MEMBER(1);

    private final int level;

    GroupRole(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAtLeast(GroupRole required) {
        return this.level >= required.level;
    }
}
