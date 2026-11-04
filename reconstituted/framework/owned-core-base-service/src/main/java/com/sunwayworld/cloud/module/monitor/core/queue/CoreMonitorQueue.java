/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.queue;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum CoreMonitorQueue {
    INSTANCE;

    private Queue<CoreMonitorServerRuntimeBean> serverRuntimeQueue = new ConcurrentLinkedQueue<CoreMonitorServerRuntimeBean>();
    private Queue<CoreMonitorAppRuntimeBean> appRuntimeQueue = new ConcurrentLinkedQueue<CoreMonitorAppRuntimeBean>();
    private Queue<CoreMonitorMessageBean> monitorMessageQueue = new ConcurrentLinkedQueue<CoreMonitorMessageBean>();
    private Queue<CoreMonitorUserBean> monitorUserQueue = new ConcurrentLinkedQueue<CoreMonitorUserBean>();
    private Queue<CoreMonitorReqBean> monitorReqQueue = new ConcurrentLinkedQueue<CoreMonitorReqBean>();

    public Queue<CoreMonitorServerRuntimeBean> getServerRuntimeQueue() {
        return this.serverRuntimeQueue;
    }

    public Queue<CoreMonitorAppRuntimeBean> getAppRuntimeQueue() {
        return this.appRuntimeQueue;
    }

    public Queue<CoreMonitorMessageBean> getMonitorMessageQueue() {
        return this.monitorMessageQueue;
    }

    public Queue<CoreMonitorUserBean> getMonitorUserQueue() {
        return this.monitorUserQueue;
    }

    public Queue<CoreMonitorReqBean> getMonitorReqQueue() {
        return this.monitorReqQueue;
    }

    public CoreMonitorServerRuntimeBean pollServerRuntime() {
        return this.serverRuntimeQueue.poll();
    }

    public void offerServerRuntime(CoreMonitorServerRuntimeBean serverRuntime) {
        this.serverRuntimeQueue.offer(serverRuntime);
    }

    public boolean isServerRuntimeQueueEmpty() {
        return this.serverRuntimeQueue.isEmpty();
    }

    public int getServerRuntimeQueueLength() {
        return this.serverRuntimeQueue.size();
    }

    public CoreMonitorAppRuntimeBean pollAppRuntime() {
        return this.appRuntimeQueue.poll();
    }

    public void offerAppRuntime(CoreMonitorAppRuntimeBean appRuntime) {
        this.appRuntimeQueue.offer(appRuntime);
    }

    public boolean isAppRuntimeQueueEmpty() {
        return this.appRuntimeQueue.isEmpty();
    }

    public int getAppRuntimeQueueLength() {
        return this.appRuntimeQueue.size();
    }

    public CoreMonitorMessageBean pollMonitorMessage() {
        return this.monitorMessageQueue.poll();
    }

    public void offerMonitorMessage(CoreMonitorMessageBean monitorMessage) {
        this.monitorMessageQueue.offer(monitorMessage);
    }

    public boolean isMonitorMessageQueueEmpty() {
        return this.monitorMessageQueue.isEmpty();
    }

    public int getMonitorMessageQueueLength() {
        return this.appRuntimeQueue.size();
    }

    public CoreMonitorUserBean pollMonitorUser() {
        return this.monitorUserQueue.poll();
    }

    public void offerMonitorUser(CoreMonitorUserBean monitorUser) {
        this.monitorUserQueue.offer(monitorUser);
    }

    public boolean isMonitorUserQueueEmpty() {
        return this.monitorUserQueue.isEmpty();
    }

    public int getMonitorUserQueueLength() {
        return this.monitorUserQueue.size();
    }

    public CoreMonitorReqBean pollMonitorReq() {
        return this.monitorReqQueue.poll();
    }

    public void offerMonitorReq(CoreMonitorReqBean monitorReq) {
        this.monitorReqQueue.offer(monitorReq);
    }

    public boolean isMonitorReqQueueEmpty() {
        return this.monitorReqQueue.isEmpty();
    }

    public int getMonitorReqQueueLength() {
        return this.monitorReqQueue.size();
    }
}
