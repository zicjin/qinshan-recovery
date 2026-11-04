/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_NOTIFICATION_INST_TO")
public class CoreNotificationInstanceToBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = -3459531198974830791L;
    @Id
    private Long id;
    private Long instId;
    private String notifiedId;
    @NotNull(defaultValue="0")
    private String read;
    @NotNull(defaultValue="0")
    private String ignore;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstId() {
        return this.instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public String getNotifiedId() {
        return this.notifiedId;
    }

    public void setNotifiedId(String notifiedId) {
        this.notifiedId = notifiedId;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getIgnore() {
        return this.ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public LocalDateTime getReadTime() {
        return this.readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
}
