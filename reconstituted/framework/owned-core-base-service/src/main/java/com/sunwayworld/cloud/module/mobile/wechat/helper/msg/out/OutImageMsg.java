/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutImageMsg
extends OutMsg {
    private String mediaId;

    public OutImageMsg() {
        this.msgType = "image";
    }

    public OutImageMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "image";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null == this.mediaId) {
            throw new NullPointerException("mediaId is null");
        }
        sb.append("<Image>\n");
        sb.append("<MediaId><![CDATA[").append(this.mediaId).append("]]></MediaId>\n");
        sb.append("</Image>\n");
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
