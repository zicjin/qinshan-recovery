/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.Text;

public class QiYeTextMsg
extends QiYeBaseMsg {
    private Text text;

    public QiYeTextMsg() {
        this.msgtype = MessageType.text.name();
    }

    public QiYeTextMsg(Text text) {
        this.text = text;
        this.msgtype = MessageType.text.name();
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}
