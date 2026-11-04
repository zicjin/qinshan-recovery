/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.token;

import com.sunwayworld.framework.security.token.ResetTokenDTO;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum ResetTokenQueue {
    INSTANCE;

    private Queue<ResetTokenDTO> queue = new ConcurrentLinkedQueue<ResetTokenDTO>();

    public ResetTokenDTO poll() {
        return this.queue.poll();
    }

    public void offer(ResetTokenDTO resetToken) {
        this.queue.offer(resetToken);
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
