/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutVideoMsg
extends OutMsg {
    private String mediaId;
    private String title;
    private String description;

    public OutVideoMsg() {
        this.msgType = "video";
    }

    public OutVideoMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "video";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null == this.mediaId) {
            throw new NullPointerException("mediaId is null");
        }
        sb.append("<Video>\n");
        sb.append("<MediaId><![CDATA[").append(this.mediaId).append("]]></MediaId>\n");
        sb.append("<Title><![CDATA[").append(this.nullToBlank(this.title)).append("]]></Title>\n");
        sb.append("<Description><![CDATA[").append(this.nullToBlank(this.description)).append("]]></Description>\n");
        sb.append("</Video>\n");
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
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
}
