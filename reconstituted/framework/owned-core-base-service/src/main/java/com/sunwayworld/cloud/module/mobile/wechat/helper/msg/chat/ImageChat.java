/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ChatBase;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ImageChatMsg;

public class ImageChat
extends ChatBase {
    private ImageChatMsg image;

    public ImageChatMsg getImage() {
        return this.image;
    }

    public void setImage(ImageChatMsg image) {
        this.image = image;
        this.setMsgtype("image");
    }
}
