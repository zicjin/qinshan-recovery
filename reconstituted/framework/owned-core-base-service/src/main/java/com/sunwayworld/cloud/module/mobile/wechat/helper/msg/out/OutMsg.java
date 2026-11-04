/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import java.io.Serializable;

public abstract class OutMsg
implements Serializable {
    protected String toUserName;
    protected String fromUserName;
    protected Integer createTime;
    protected String msgType;

    public OutMsg(InMsg inMsg) {
        this.toUserName = inMsg.getFromUserName();
        this.fromUserName = inMsg.getToUserName();
        this.createTime = this.now();
    }

    public OutMsg() {
    }

    protected abstract void subXml(StringBuilder var1);

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>\n");
        sb.append("<ToUserName><![CDATA[").append(this.toUserName).append("]]></ToUserName>\n");
        sb.append("<FromUserName><![CDATA[").append(this.fromUserName).append("]]></FromUserName>\n");
        sb.append("<CreateTime>").append(this.createTime).append("</CreateTime>\n");
        sb.append("<MsgType><![CDATA[").append(this.msgType).append("]]></MsgType>\n");
        this.subXml(sb);
        sb.append("</xml>");
        return sb.toString();
    }

    protected String nullToBlank(String str) {
        return null == str ? "" : str;
    }

    public Integer now() {
        return (int)(System.currentTimeMillis() / 1000L);
    }

    public String getToUserName() {
        return this.toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return this.fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Integer getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return this.msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
