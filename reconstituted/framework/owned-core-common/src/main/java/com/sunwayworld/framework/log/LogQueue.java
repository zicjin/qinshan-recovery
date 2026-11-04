/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.log;

import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum LogQueue {
    INSTANCE;

    private Map<String, Queue<CoreLogBean>> box = new ConcurrentHashMap<String, Queue<CoreLogBean>>();

    public CoreLogBean poll() {
        Queue<CoreLogBean> queue = this.box.get(TenantContext.getTenant());
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void offer(CoreLogBean log) {
        Queue<CoreLogBean> queue = this.box.get(TenantContext.getTenant());
        if (queue == null) {
            Map<String, Queue<CoreLogBean>> map = this.box;
            synchronized (map) {
                if (queue == null) {
                    queue = new ConcurrentLinkedQueue<CoreLogBean>();
                    this.box.put(TenantContext.getTenant(), queue);
                }
            }
        }
        queue.offer(log);
    }

    public boolean isEmpty() {
        Queue<CoreLogBean> queue = this.box.get(TenantContext.getTenant());
        return queue == null || queue.isEmpty();
    }
}
