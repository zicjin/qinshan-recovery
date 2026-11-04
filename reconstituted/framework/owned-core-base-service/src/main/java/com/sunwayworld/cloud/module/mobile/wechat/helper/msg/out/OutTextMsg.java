/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutTextMsg
extends OutMsg {
    private String content;

    public OutTextMsg() {
        this.msgType = "text";
    }

    public OutTextMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "text";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null == this.content) {
            throw new NullPointerException("content is null");
        }
        sb.append("<Content><![CDATA[").append(this.content).append("]]></Content>\n");
    }

    public String getContent() {
        return this.content;
    }

    public OutTextMsg setContent(String content) {
        this.content = content;
        return this;
    }
}
