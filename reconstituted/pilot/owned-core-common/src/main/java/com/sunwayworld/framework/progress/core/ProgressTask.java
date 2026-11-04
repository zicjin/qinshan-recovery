/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.core;

public abstract class ProgressTask {
    private final String spanName;

    public ProgressTask(String spanName) {
        this.spanName = spanName;
    }

    public String getSpanName() {
        return this.spanName;
    }

    public abstract void run();

    public void rollback() {
    }

    public void rollback(Exception ex) {
        this.rollback();
    }
}
