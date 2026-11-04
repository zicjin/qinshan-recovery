/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.TransInfo;
import com.sunwayworld.framework.utils.StringUtils;

public class OutCustomMsg
extends OutMsg {
    private TransInfo transInfo;

    public OutCustomMsg() {
        this.msgType = "transfer_customer_service";
    }

    public OutCustomMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "transfer_customer_service";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        if (null != this.transInfo && !StringUtils.isBlank(this.transInfo.getKfAccount())) {
            sb.append("<TransInfo>\n");
            sb.append("<KfAccount><![CDATA[").append(this.transInfo.getKfAccount()).append("]]></KfAccount>\n");
            sb.append("</TransInfo>\n");
        }
    }

    public TransInfo getTransInfo() {
        return this.transInfo;
    }

    public void setTransInfo(TransInfo transInfo) {
        this.transInfo = transInfo;
    }
}
