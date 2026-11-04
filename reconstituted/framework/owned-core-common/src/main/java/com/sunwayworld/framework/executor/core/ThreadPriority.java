/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor.core;

public enum ThreadPriority {
    Highest(4),
    AboveNormal(3),
    Normal(2),
    BelowNormal(1),
    Lowest(0);

    private final int value;

    private ThreadPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
