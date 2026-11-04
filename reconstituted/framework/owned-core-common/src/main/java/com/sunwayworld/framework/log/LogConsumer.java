/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.log;

import com.sunwayworld.framework.log.LogQueue;
import com.sunwayworld.framework.tenant.annotation.Tenant;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import com.sunwayworld.module.sys.log.service.CoreLogService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogConsumer {
    private static final int MAX_INSERT_SIZE = 50;
    @Autowired
    private CoreLogService logService;

    @Tenant
    @Scheduled(fixedDelay=1000L)
    public void consume() {
        LogQueue queue = LogQueue.INSTANCE;
        ArrayList<CoreLogBean> insertLogList = new ArrayList<CoreLogBean>();
        while (!queue.isEmpty()) {
            CoreLogBean log = queue.poll();
            insertLogList.add(log);
            if (insertLogList.size() < 50) continue;
            this.logService.insertLog(insertLogList);
            insertLogList.clear();
        }
        if (!insertLogList.isEmpty()) {
            this.logService.insertLog(insertLogList);
            insertLogList.clear();
        }
    }
}
