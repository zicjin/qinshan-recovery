/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

public enum CoreMonitorReqCache {
    INSTANCE;

    private LongAdder tps = new LongAdder();
    private LongAdder qps = new LongAdder();
    private Set<String> concurrentUserSet = new HashSet<String>();

    public Long getTps() {
        return this.tps.sum();
    }

    public Long getQps() {
        return this.qps.sum();
    }

    public Set<String> getCus() {
        return this.concurrentUserSet;
    }

    public void increaseTps() {
        this.tps.add(1L);
    }

    public void increaseQps() {
        this.qps.add(1L);
    }

    public void increaseCus(String userId) {
        this.concurrentUserSet.add(userId);
    }

    public void clearTps() {
        this.tps.reset();
    }

    public void clearQps() {
        this.qps.reset();
    }

    public void clearCus() {
        this.concurrentUserSet.clear();
    }
}
