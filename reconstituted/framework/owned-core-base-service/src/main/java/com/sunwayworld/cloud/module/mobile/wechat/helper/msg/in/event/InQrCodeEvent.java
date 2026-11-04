/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InQrCodeEvent
extends EventInMsg {
    public static final String EVENT_INQRCODE_SUBSCRIBE = "subscribe";
    public static final String EVENT_INQRCODE_SCAN = "SCAN";
    private String eventKey;
    private String ticket;

    public InQrCodeEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
