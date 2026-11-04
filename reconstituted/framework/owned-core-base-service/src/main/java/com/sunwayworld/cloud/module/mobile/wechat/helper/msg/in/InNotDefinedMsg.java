/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InNotDefinedMsg
extends InMsg {
    protected transient XmlHelper xmlHelper;

    public InNotDefinedMsg(String toUserName, String fromUserName, Integer createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public XmlHelper getXmlHelper() {
        return this.xmlHelper;
    }

    public void setXmlHelper(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }
}
