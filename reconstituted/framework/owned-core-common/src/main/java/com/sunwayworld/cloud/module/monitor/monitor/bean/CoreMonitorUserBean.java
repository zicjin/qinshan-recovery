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

@Table(value="T_CORE_MONITOR_USER")
public class CoreMonitorUserBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -5734548553397358140L;
    @Id
    private Long id;
    @NotNull(defaultValue="0")
    private Long onlineUserQty;
    @NotNull(defaultValue="0")
    private Long concurrentUserQty;
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

    public Long getOnlineUserQty() {
        return this.onlineUserQty;
    }

    public void setOnlineUserQty(Long onlineUserQty) {
        this.onlineUserQty = onlineUserQty;
    }

    public Long getConcurrentUserQty() {
        return this.concurrentUserQty;
    }

    public void setConcurrentUserQty(Long concurrentUserQty) {
        this.concurrentUserQty = concurrentUserQty;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
