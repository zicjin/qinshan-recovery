/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InTemplateMsgEvent
extends EventInMsg {
    public static final String EVENT_INTEMPLATEMSG_STATUS_SUCCESS = "success";
    public static final String EVENT_INTEMPLATEMSG_STATUS_BLOCK = "block";
    public static final String EVENT_INTEMPLATEMSG_STATUS_FAILED = "failed: system failed";
    private String msgId;
    private String status;

    public InTemplateMsgEvent(String toUserName, String fromUserName, Integer createTime, String event) {
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
}
