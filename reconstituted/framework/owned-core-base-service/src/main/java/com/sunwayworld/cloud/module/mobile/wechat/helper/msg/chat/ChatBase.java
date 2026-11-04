/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ChatReceiver;

public abstract class ChatBase {
    private ChatReceiver receiver;
    private String sender;
    private String msgtype;

    public ChatReceiver getReceiver() {
        return this.receiver;
    }

    public void setReceiver(ChatReceiver receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
