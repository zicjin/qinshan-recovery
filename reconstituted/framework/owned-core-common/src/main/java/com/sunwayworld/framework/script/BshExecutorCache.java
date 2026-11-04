/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.script;

import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.script.exeception.BshException;
import java.util.concurrent.LinkedBlockingQueue;

public enum BshExecutorCache {
    INSTANCE;

    private final transient LinkedBlockingQueue<BshExecutor> cache = new LinkedBlockingQueue();

    public void add(BshExecutor executor) {
        this.cache.add(executor);
    }

    public BshExecutor take() {
        try {
            if (this.cache.size() == 0) {
                this.cache.add(BshExecutor.createNewInstance());
            }
            return this.cache.take();
        }
        catch (InterruptedException ie) {
            throw new BshException(ie);
        }
    }

    public LinkedBlockingQueue<BshExecutor> getCache() {
        return this.cache;
    }
}
