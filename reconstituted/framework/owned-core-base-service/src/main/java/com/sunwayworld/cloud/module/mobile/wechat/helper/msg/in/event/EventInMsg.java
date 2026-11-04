/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;

public abstract class EventInMsg
extends InMsg {
    private static final String MSG_TYPE = "event";
    protected String event;

    public EventInMsg(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, MSG_TYPE);
        this.event = event;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
