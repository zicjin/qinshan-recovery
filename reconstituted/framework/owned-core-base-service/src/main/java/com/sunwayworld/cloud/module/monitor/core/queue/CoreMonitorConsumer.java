/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.queue;

import com.sunwayworld.cloud.module.monitor.core.queue.CoreMonitorQueue;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppRuntimeService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorMessageService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorReqService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerRuntimeService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorUserService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile(value={"monitor"})
public class CoreMonitorConsumer {
    private static final int MAX_INSERT_SIZE = 50;
    @Autowired
    private CoreMonitorServerRuntimeService serverRuntimeService;
    @Autowired
    private CoreMonitorAppRuntimeService appRuntimeService;
    @Autowired
    private CoreMonitorReqService monitorReqService;
    @Autowired
    private CoreMonitorUserService monitorUserService;
    @Autowired
    private CoreMonitorMessageService monitorMessageService;

    @Scheduled(fixedDelay=1000L)
    public void consumeServerRuntime() {
        ArrayList<CoreMonitorServerRuntimeBean> insertList = new ArrayList<CoreMonitorServerRuntimeBean>();
        while (!CoreMonitorQueue.INSTANCE.isServerRuntimeQueueEmpty()) {
            CoreMonitorServerRuntimeBean serverRuntime = CoreMonitorQueue.INSTANCE.pollServerRuntime();
            insertList.add(serverRuntime);
            if (insertList.size() < 50) continue;
            this.serverRuntimeService.insertServerRuntime(insertList);
            insertList.clear();
        }
        if (!insertList.isEmpty()) {
            this.serverRuntimeService.insertServerRuntime(insertList);
            insertList.clear();
        }
    }

    @Scheduled(fixedDelay=1000L)
    public void consumeAppRuntime() {
        ArrayList<CoreMonitorAppRuntimeBean> insertList = new ArrayList<CoreMonitorAppRuntimeBean>();
        while (!CoreMonitorQueue.INSTANCE.isAppRuntimeQueueEmpty()) {
            CoreMonitorAppRuntimeBean appRuntime = CoreMonitorQueue.INSTANCE.pollAppRuntime();
            insertList.add(appRuntime);
            if (insertList.size() < 50) continue;
            this.appRuntimeService.insertAppRuntime(insertList);
            insertList.clear();
        }
        if (!insertList.isEmpty()) {
            this.appRuntimeService.insertAppRuntime(insertList);
            insertList.clear();
        }
    }

    @Scheduled(fixedDelay=1000L)
    public void consumeMonitorReq() {
        ArrayList<CoreMonitorReqBean> insertList = new ArrayList<CoreMonitorReqBean>();
        while (!CoreMonitorQueue.INSTANCE.isMonitorReqQueueEmpty()) {
            CoreMonitorReqBean monitorReq = CoreMonitorQueue.INSTANCE.pollMonitorReq();
            insertList.add(monitorReq);
            if (insertList.size() < 50) continue;
            this.monitorReqService.insertMonitorReq(insertList);
            insertList.clear();
        }
        if (!insertList.isEmpty()) {
            this.monitorReqService.insertMonitorReq(insertList);
            insertList.clear();
        }
    }

    @Scheduled(fixedDelay=1000L)
    public void consumeMonitorUser() {
        ArrayList<CoreMonitorUserBean> insertList = new ArrayList<CoreMonitorUserBean>();
        while (!CoreMonitorQueue.INSTANCE.isMonitorUserQueueEmpty()) {
            CoreMonitorUserBean monitorUser = CoreMonitorQueue.INSTANCE.pollMonitorUser();
            insertList.add(monitorUser);
            if (insertList.size() < 50) continue;
            this.monitorUserService.insertMonitorUser(insertList);
            insertList.clear();
        }
        if (!insertList.isEmpty()) {
            this.monitorUserService.insertMonitorUser(insertList);
            insertList.clear();
        }
    }

    @Scheduled(fixedDelay=1000L)
    public void consumeMonitorMessage() {
        ArrayList<CoreMonitorMessageBean> insertList = new ArrayList<CoreMonitorMessageBean>();
        while (!CoreMonitorQueue.INSTANCE.isMonitorMessageQueueEmpty()) {
            CoreMonitorMessageBean monitorMessage = CoreMonitorQueue.INSTANCE.pollMonitorMessage();
            insertList.add(monitorMessage);
            if (insertList.size() < 50) continue;
            this.monitorMessageService.insertMonitorMessage(insertList);
            insertList.clear();
        }
        if (!insertList.isEmpty()) {
            this.monitorMessageService.insertMonitorMessage(insertList);
            insertList.clear();
        }
    }
}
