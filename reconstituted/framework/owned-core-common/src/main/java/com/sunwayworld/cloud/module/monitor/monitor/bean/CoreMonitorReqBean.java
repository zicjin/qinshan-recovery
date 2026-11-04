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

@Table(value="T_CORE_MONITOR_REQ")
public class CoreMonitorReqBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -8353350311513174118L;
    @Id
    private Long id;
    @NotNull(defaultValue="0")
    private Long qps;
    @NotNull(defaultValue="0")
    private Long tps;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getQps() {
        return this.qps;
    }

    public void setQps(Long qps) {
        this.qps = qps;
    }

    public Long getTps() {
        return this.tps;
    }

    public void setTps(Long tps) {
        this.tps = tps;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
