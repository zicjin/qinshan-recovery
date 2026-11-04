/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InFollowEvent
extends EventInMsg {
    public static final String EVENT_INFOLLOW_SUBSCRIBE = "subscribe";
    public static final String EVENT_INFOLLOW_UNSUBSCRIBE = "unsubscribe";
    private String eventKey;

    public InFollowEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public InFollowEvent(String toUserName, String fromUserName, Integer createTime, String event, String eventKey) {
        super(toUserName, fromUserName, createTime, event);
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}
