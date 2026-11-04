/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ChatBase;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.TextChatMsg;

public class TextChat
extends ChatBase {
    private TextChatMsg text;

    public TextChatMsg getText() {
        return this.text;
    }

    public void setText(TextChatMsg text) {
        this.text = text;
        this.setMsgtype("text");
    }
}
