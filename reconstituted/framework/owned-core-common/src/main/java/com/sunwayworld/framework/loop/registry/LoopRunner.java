/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.loop.registry;

public interface LoopRunner {
    public void run();

    public boolean active();

    default public boolean enable() {
        return true;
    }
}
