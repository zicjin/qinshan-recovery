/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreMonitorConfigDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 1463205213952392282L;
    private Long serverCpuAlarm;
    private Long serverCpuAbnormal;
    private Long serverRamAlarm;
    private Long serverRamAbnormal;
    private Long serverDiskAlarm;
    private Long serverDiskAbnormal;
    private Long appCpuAlarm;
    private Long appCpuAbnormal;
    private Long appRamAlarm;
    private Long appRamAbnormal;
    private Long requestQpsAlarm;
    private Long requestQpsAbnormal;
    private Long requestTpsAlarm;
    private Long requestTpsAbnormal;

    public Long getServerCpuAlarm() {
        return this.serverCpuAlarm;
    }

    public void setServerCpuAlarm(Long serverCpuAlarm) {
        this.serverCpuAlarm = serverCpuAlarm;
    }

    public Long getServerCpuAbnormal() {
        return this.serverCpuAbnormal;
    }

    public void setServerCpuAbnormal(Long serverCpuAbnormal) {
        this.serverCpuAbnormal = serverCpuAbnormal;
    }

    public Long getServerRamAlarm() {
        return this.serverRamAlarm;
    }

    public void setServerRamAlarm(Long serverRamAlarm) {
        this.serverRamAlarm = serverRamAlarm;
    }

    public Long getServerRamAbnormal() {
        return this.serverRamAbnormal;
    }

    public void setServerRamAbnormal(Long serverRamAbnormal) {
        this.serverRamAbnormal = serverRamAbnormal;
    }

    public Long getServerDiskAlarm() {
        return this.serverDiskAlarm;
    }

    public void setServerDiskAlarm(Long serverDiskAlarm) {
        this.serverDiskAlarm = serverDiskAlarm;
    }

    public Long getServerDiskAbnormal() {
        return this.serverDiskAbnormal;
    }

    public void setServerDiskAbnormal(Long serverDiskAbnormal) {
        this.serverDiskAbnormal = serverDiskAbnormal;
    }

    public Long getAppCpuAlarm() {
        return this.appCpuAlarm;
    }

    public void setAppCpuAlarm(Long appCpuAlarm) {
        this.appCpuAlarm = appCpuAlarm;
    }

    public Long getAppCpuAbnormal() {
        return this.appCpuAbnormal;
    }

    public void setAppCpuAbnormal(Long appCpuAbnormal) {
        this.appCpuAbnormal = appCpuAbnormal;
    }

    public Long getAppRamAlarm() {
        return this.appRamAlarm;
    }

    public void setAppRamAlarm(Long appRamAlarm) {
        this.appRamAlarm = appRamAlarm;
    }

    public Long getAppRamAbnormal() {
        return this.appRamAbnormal;
    }

    public void setAppRamAbnormal(Long appRamAbnormal) {
        this.appRamAbnormal = appRamAbnormal;
    }

    public Long getRequestQpsAlarm() {
        return this.requestQpsAlarm;
    }

    public void setRequestQpsAlarm(Long requestQpsAlarm) {
        this.requestQpsAlarm = requestQpsAlarm;
    }

    public Long getRequestQpsAbnormal() {
        return this.requestQpsAbnormal;
    }

    public void setRequestQpsAbnormal(Long requestQpsAbnormal) {
        this.requestQpsAbnormal = requestQpsAbnormal;
    }

    public Long getRequestTpsAlarm() {
        return this.requestTpsAlarm;
    }

    public void setRequestTpsAlarm(Long requestTpsAlarm) {
        this.requestTpsAlarm = requestTpsAlarm;
    }

    public Long getRequestTpsAbnormal() {
        return this.requestTpsAbnormal;
    }

    public void setRequestTpsAbnormal(Long requestTpsAbnormal) {
        this.requestTpsAbnormal = requestTpsAbnormal;
    }
}
