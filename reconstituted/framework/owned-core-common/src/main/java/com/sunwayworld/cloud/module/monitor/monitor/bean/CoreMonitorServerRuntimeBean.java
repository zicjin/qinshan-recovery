/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;

@Table(value="T_CORE_MONITOR_SERVER_RUNTIME")
public class CoreMonitorServerRuntimeBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -854176908307818578L;
    @Id
    private Long id;
    private String serverId;
    private Long serverCpuUtilization;
    private Long serverRamUtilization;
    private Long serverDiskUtilization;
    private LocalDateTime createdTime;
    @NotNull(defaultValue="normal")
    private String serverCpuStatus;
    @NotNull(defaultValue="normal")
    private String serverRamStatus;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Long getServerCpuUtilization() {
        return this.serverCpuUtilization;
    }

    public void setServerCpuUtilization(Long serverCpuUtilization) {
        this.serverCpuUtilization = serverCpuUtilization;
    }

    public Long getServerRamUtilization() {
        return this.serverRamUtilization;
    }

    public void setServerRamUtilization(Long serverRamUtilization) {
        this.serverRamUtilization = serverRamUtilization;
    }

    public Long getServerDiskUtilization() {
        return this.serverDiskUtilization;
    }

    public void setServerDiskUtilization(Long serverDiskUtilization) {
        this.serverDiskUtilization = serverDiskUtilization;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getServerCpuStatus() {
        return this.serverCpuStatus;
    }

    public void setServerCpuStatus(String serverCpuStatus) {
        this.serverCpuStatus = serverCpuStatus;
    }

    public String getServerRamStatus() {
        return this.serverRamStatus;
    }

    public void setServerRamStatus(String serverRamStatus) {
        this.serverRamStatus = serverRamStatus;
    }
}
