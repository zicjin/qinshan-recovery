/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.msg;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InEqubindEvent
extends EventInMsg {
    private static final long serialVersionUID = 2579633450665411023L;
    public static final String DEVICE_EVENT = "device_event";
    public static final String UNBIND = "unbind";
    public static final String BIND = "bind";
    private String deviceType;
    private String deviceID;
    private String openID;
    private String sessionID;

    public InEqubindEvent(String toUserName, String fromUserName, Integer createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType);
        this.setEvent(event);
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getOpenID() {
        return this.openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
