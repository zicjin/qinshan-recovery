/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InMassEvent
extends EventInMsg {
    public static final String EVENT_INMASS_STATUS_SENDSUCCESS = "sendsuccess";
    public static final String EVENT_INMASS_STATUS_SENDFAIL = "sendfail";
    private String msgId;
    private String status;
    private String totalCount;
    private String filterCount;
    private String sentCount;
    private String errorCount;

    public InMassEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getFilterCount() {
        return this.filterCount;
    }

    public void setFilterCount(String filterCount) {
        this.filterCount = filterCount;
    }

    public String getSentCount() {
        return this.sentCount;
    }

    public void setSentCount(String sentCount) {
        this.sentCount = sentCount;
    }

    public String getErrorCount() {
        return this.errorCount;
    }

    public void setErrorCount(String errorCount) {
        this.errorCount = errorCount;
    }
}
