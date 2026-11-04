/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.log;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.module.sys.menu.log.CoreMenuAccessLogQueue;
import com.sunwayworld.module.sys.menu.log.bean.CoreMenuAccessLogDTO;
import com.sunwayworld.module.sys.menu.service.CoreMenuAccessLogService;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class CoreMenuAccessLogLoopRunner
implements LoopRunner {
    private static final int MAX_CONSUME_QTY = 1000;
    private AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    @Override
    public void run() {
        CoreMenuAccessLogQueue queue = CoreMenuAccessLogQueue.INSTANCE;
        ArrayList<CoreMenuAccessLogDTO> accessLogList = new ArrayList<CoreMenuAccessLogDTO>();
        while (!queue.isEmpty() && accessLogList.size() < 1000) {
            CoreMenuAccessLogDTO accessLog = queue.poll();
            if (accessLogList.contains(accessLog)) {
                CoreMenuAccessLogDTO existingAccessLog = accessLogList.stream().filter(l -> l.equals(accessLog)).findAny().get();
                existingAccessLog.increaseAccessTimes();
                continue;
            }
            accessLogList.add(accessLog);
        }
        if (!accessLogList.isEmpty()) {
            CoreMenuAccessLogService accessLogService = ApplicationContextHelper.getBean(CoreMenuAccessLogService.class);
            accessLogList.forEach(l -> accessLogService.insertMenuAccessLog(l.getUserId(), l.getMenuId(), l.getAccessTimes()));
        }
        this.counter.set(System.currentTimeMillis());
    }

    @Override
    public boolean active() {
        return this.counter.get() + 10000L < System.currentTimeMillis();
    }

    @Override
    public boolean enable() {
        return ApplicationContextHelper.getEnvironment().getProperty("sunway.shortcut-menu.auto.enabled", Boolean.class, false);
    }
}
