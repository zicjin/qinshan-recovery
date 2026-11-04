/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_MONITOR_APP_RUNTIME")
public class CoreMonitorAppRuntimeBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = 8841304116490412984L;
    @Id
    private Long id;
    private String appId;
    private String serverId;
    private Long serverRunTimeId;
    private Long appCpuUtilization;
    private Long appRamUtilization;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @NotNull(defaultValue="normal")
    private String appCpuStatus;
    @NotNull(defaultValue="normal")
    private String appRamStatus;
    @NotNull(defaultValue="running")
    private String status;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Long getServerRunTimeId() {
        return this.serverRunTimeId;
    }

    public void setServerRunTimeId(Long serverRunTimeId) {
        this.serverRunTimeId = serverRunTimeId;
    }

    public Long getAppCpuUtilization() {
        return this.appCpuUtilization;
    }

    public void setAppCpuUtilization(Long appCpuUtilization) {
        this.appCpuUtilization = appCpuUtilization;
    }

    public Long getAppRamUtilization() {
        return this.appRamUtilization;
    }

    public void setAppRamUtilization(Long appRamUtilization) {
        this.appRamUtilization = appRamUtilization;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getAppCpuStatus() {
        return this.appCpuStatus;
    }

    public void setAppCpuStatus(String appCpuStatus) {
        this.appCpuStatus = appCpuStatus;
    }

    public String getAppRamStatus() {
        return this.appRamStatus;
    }

    public void setAppRamStatus(String appRamStatus) {
        this.appRamStatus = appRamStatus;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
