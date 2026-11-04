/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InCustomEvent
extends EventInMsg {
    public static final String EVENT_INCUSTOM_KF_CREATE_SESSION = "kf_create_session";
    public static final String EVENT_INCUSTOM_KF_CLOSE_SESSION = "kf_close_session";
    public static final String EVENT_INCUSTOM_KF_SWITCH_SESSION = "kf_switch_session";
    private String kfAccount;
    private String toKfAccount;

    public InCustomEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getKfAccount() {
        return this.kfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.kfAccount = kfAccount;
    }

    public String getToKfAccount() {
        return this.toKfAccount;
    }

    public void setToKfAccount(String toKfAccount) {
        this.toKfAccount = toKfAccount;
    }
}
