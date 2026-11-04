/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.log;

import com.sunwayworld.module.sys.menu.log.bean.CoreMenuAccessLogDTO;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum CoreMenuAccessLogQueue {
    INSTANCE;

    private Queue<CoreMenuAccessLogDTO> queue = new ConcurrentLinkedQueue<CoreMenuAccessLogDTO>();

    public CoreMenuAccessLogDTO poll() {
        return this.queue.poll();
    }

    public void offer(CoreMenuAccessLogDTO log) {
        this.queue.offer(log);
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
