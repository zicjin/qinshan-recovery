/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.speech_recognition;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InVoiceMsg;

public class InSpeechRecognitionResults
extends InVoiceMsg {
    private String recognition;

    public InSpeechRecognitionResults(String toUserName, String fromUserName, Integer createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getRecognition() {
        return this.recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }
}
