/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.msg;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutEquDataMsg
extends OutMsg {
    private static final long serialVersionUID = -1187439400934008473L;
    private String DeviceType;
    private String DeviceID;
    private String content;
    private String SessionID;

    public OutEquDataMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "device_text";
    }

    public OutEquDataMsg() {
        this.msgType = "device_text";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null == this.content) {
            throw new NullPointerException("content is null");
        }
        sb.append("<DeviceType><![CDATA[").append(this.DeviceType).append("]]></DeviceType>\n");
        sb.append("<DeviceID><![CDATA[").append(this.DeviceID).append("]]></DeviceID>\n");
        sb.append("<SessionID><![CDATA[").append(this.SessionID).append("]]></SessionID>\n");
        sb.append("<Content><![CDATA[").append(this.content).append("]]></Content>\n");
    }

    public String getDeviceType() {
        return this.DeviceType;
    }

    public void setDeviceType(String deviceType) {
        this.DeviceType = deviceType;
    }

    public String getDeviceID() {
        return this.DeviceID;
    }

    public void setDeviceID(String deviceID) {
        this.DeviceID = deviceID;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSessionID() {
        return this.SessionID;
    }

    public void setSessionID(String sessionID) {
        this.SessionID = sessionID;
    }
}
