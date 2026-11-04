/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutVoiceMsg
extends OutMsg {
    private String mediaId;

    public OutVoiceMsg() {
        this.msgType = "voice";
    }

    public OutVoiceMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "voice";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null == this.mediaId) {
            throw new NullPointerException("mediaId is null");
        }
        sb.append("<Voice>\n");
        sb.append("<MediaId><![CDATA[").append(this.mediaId).append("]]></MediaId>\n");
        sb.append("</Voice>\n");
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
