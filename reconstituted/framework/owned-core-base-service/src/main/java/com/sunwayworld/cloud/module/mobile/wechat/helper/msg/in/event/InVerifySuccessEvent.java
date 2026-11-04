/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InVerifySuccessEvent
extends EventInMsg {
    public static final String EVENT_IN_QUALIFICATION_VERIFY_SUCCESS = "qualification_verify_success";
    public static final String EVENT_IN_NAMING_VERIFY_SUCCESS = "naming_verify_success";
    public static final String EVENT_IN_ANNUAL_RENEW = "annual_renew";
    public static final String EVENT_IN_VERIFY_EXPIRED = "verify_expired";
    private String expiredTime;

    public InVerifySuccessEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getExpiredTime() {
        return this.expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }
}
