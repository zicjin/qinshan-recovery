/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InVerifyFailEvent
extends EventInMsg {
    public static final String EVENT_IN_QUALIFICATION_VERIFY_FAIL = "qualification_verify_fail";
    public static final String EVENT_IN_NAMING_VERIFY_FAIL = "naming_verify_fail";
    private String failTime;
    private String failReason;

    public InVerifyFailEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getFailTime() {
        return this.failTime;
    }

    public void setFailTime(String failTime) {
        this.failTime = failTime;
    }

    public String getFailReason() {
        return this.failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
