/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;

public class QiYeFileMsg
extends QiYeBaseMsg {
    private String media_id;

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String mediaId) {
        this.media_id = mediaId;
    }

    public QiYeFileMsg(String mediaId) {
        this.media_id = mediaId;
        this.msgtype = MessageType.file.name();
    }

    public QiYeFileMsg() {
        this.msgtype = MessageType.file.name();
    }
}
