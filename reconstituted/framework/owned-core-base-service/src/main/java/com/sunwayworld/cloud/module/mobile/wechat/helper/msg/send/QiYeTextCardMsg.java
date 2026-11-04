/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.TextCard;

public class QiYeTextCardMsg
extends QiYeBaseMsg {
    private TextCard textcard;

    public QiYeTextCardMsg() {
        this.msgtype = MessageType.textcard.name();
    }

    public QiYeTextCardMsg(String title, String description, String url, String btntext) {
        this.textcard = new TextCard(title, description, url, btntext);
        this.msgtype = MessageType.textcard.name();
    }

    public TextCard getTextcard() {
        return this.textcard;
    }

    public void setTextcard(TextCard textcard) {
        this.textcard = textcard;
    }
}
