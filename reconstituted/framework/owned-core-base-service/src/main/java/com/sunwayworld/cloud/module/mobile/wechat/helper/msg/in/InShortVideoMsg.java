/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;

public class InShortVideoMsg
extends InMsg {
    private String mediaId;
    private String thumbMediaId;
    private String msgId;

    public InShortVideoMsg(String toUserName, String fromUserName, Integer createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getThumbMediaId() {
        return this.thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
