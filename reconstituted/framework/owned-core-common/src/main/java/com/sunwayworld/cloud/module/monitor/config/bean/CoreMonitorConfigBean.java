/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_MONITOR_CONFIG")
public class CoreMonitorConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = -2141304657898228894L;
    @Id
    private String id;
    private Long serverCpuAlarm;
    private Long serverCpuAbnormal;
    private Long serverRamAlarm;
    private Long serverRamAbnormal;
    private Long appCpuAlarm;
    private Long appCpuAbnormal;
    private Long appRamAlarm;
    private Long appRamAbnormal;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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
}
