/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.task.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.task.TaskRunner;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRunnerImpl
implements TaskRunner {
    @Override
    public void synchronizedRun(String identity, Runnable run) {
        if (RedisHelper.putIfAbsent("SYNCH_TASK_RUNNER", identity, "1", 1800)) {
            try {
                run.run();
            }
            finally {
                RedisHelper.evict("SYNCH_TASK_RUNNER", identity);
            }
        }
    }
}
