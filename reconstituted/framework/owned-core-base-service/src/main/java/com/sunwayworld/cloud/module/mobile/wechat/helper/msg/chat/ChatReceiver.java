/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

public class ChatReceiver {
    private String type;
    private String id;

    public String getType() {
        return this.type;
    }

    public void setType(ChatType type) {
        this.type = type.name();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static enum ChatType {
        single,
        group;

    }
}
