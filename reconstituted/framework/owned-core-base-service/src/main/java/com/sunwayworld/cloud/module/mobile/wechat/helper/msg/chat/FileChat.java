/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ChatBase;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.FileChatMsg;

public class FileChat
extends ChatBase {
    private FileChatMsg file;

    public FileChatMsg getFile() {
        return this.file;
    }

    public void setFile(FileChatMsg file) {
        this.file = file;
        this.setMsgtype("file");
    }
}
