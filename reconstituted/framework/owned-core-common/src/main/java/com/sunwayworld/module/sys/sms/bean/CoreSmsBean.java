/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;

@Table(value="T_CORE_SMS")
public class CoreSmsBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = 8718780887671173705L;
    @Id
    private Long id;
    private String type;
    private String user;
    private String mobile;
    private String message;
    @NotNull(defaultValue="0")
    private Long retryQty;
    @NotNull(defaultValue="0")
    private String status;
    private LocalDateTime sendTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRetryQty() {
        return this.retryQty;
    }

    public void setRetryQty(Long retryQty) {
        this.retryQty = retryQty;
    }

    public LocalDateTime getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
