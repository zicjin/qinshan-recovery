/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.script;

import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.script.BshExecutorCache;
import java.util.concurrent.LinkedBlockingQueue;

public class BshExecutorCacheLoopRunner
implements LoopRunner {
    private static final int MAX_CACHE_SIZE = 50;
    private static final int MIN_CREATE_SIZE = 10;

    @Override
    public void run() {
        LinkedBlockingQueue<BshExecutor> queue = BshExecutorCache.INSTANCE.getCache();
        int qty = Math.max(10, 50 - queue.size());
        for (int i = 0; i < qty; ++i) {
            BshExecutorCache.INSTANCE.add(BshExecutor.createNewInstance());
        }
    }

    @Override
    public boolean active() {
        LinkedBlockingQueue<BshExecutor> queue = BshExecutorCache.INSTANCE.getCache();
        return queue.size() < 50;
    }
}
