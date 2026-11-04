/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;

public class QiYeVideoMsg
extends QiYeBaseMsg {
    private String media_id;
    private String title;
    private String description;

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String mediaId) {
        this.media_id = mediaId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QiYeVideoMsg(String mediaId, String title, String description) {
        this.media_id = mediaId;
        this.title = title;
        this.description = description;
        this.msgtype = MessageType.video.name();
    }
}
