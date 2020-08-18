package com.adam.lumberjack;

public enum ObjectCategory {
    PLAYER((short) 1),
    TREE((short) 2),
    MOB((short) 4),
    LOOT((short) 8),
    PLAYER_SENSOR((short) 16),
    MOB_SENSOR((short) 32),
    FENCE((short) 64),
    TRAP((short) 128),
    BORDER((short) 256),
    HUT((short) 512),
    STONE((short) 1024),
    BUILD_SENSOR((short) 2048),
    BRIDGE((short) 4096),
    RIVER((short) 8192);

    private final short value;

    ObjectCategory(final short newValue) {
        value = newValue;
    }

    public short getValue() { return value; }
}
