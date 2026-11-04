/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.ChatBase;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.chat.VoiceChatMsg;

public class VocieChat
extends ChatBase {
    private VoiceChatMsg voice;

    public VoiceChatMsg getVoice() {
        return this.voice;
    }

    public void setVoice(VoiceChatMsg voice) {
        this.voice = voice;
        this.setMsgtype("voice");
    }
}
