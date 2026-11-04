/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.msg;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;

public class InEquDataMsg
extends InMsg {
    private static final long serialVersionUID = 8591751084774828448L;
    public static final String DEVICE_TEXT = "device_text";
    private String deviceType;
    private String deviceID;
    private String content;
    private String msgId;
    private String sessionID;
    private String openID;

    public InEquDataMsg(String toUserName, String fromUserName, Integer createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
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

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getOpenID() {
        return this.openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }
}
