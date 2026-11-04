/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;

public class OutMusicMsg
extends OutMsg {
    private String title;
    private String description;
    private String musicUrl;
    private String hqMusicUrl;
    private String funcFlag = "0";

    public OutMusicMsg() {
        this.msgType = "music";
    }

    public OutMusicMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "music";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        sb.append("<Music>\n");
        sb.append("<Title><![CDATA[").append(this.nullToBlank(this.title)).append("]]></Title>\n");
        sb.append("<Description><![CDATA[").append(this.nullToBlank(this.description)).append("]]></Description>\n");
        sb.append("<MusicUrl><![CDATA[").append(this.nullToBlank(this.musicUrl)).append("]]></MusicUrl>\n");
        sb.append("<HQMusicUrl><![CDATA[").append(this.nullToBlank(this.hqMusicUrl)).append("]]></HQMusicUrl>\n");
        sb.append("<FuncFlag>").append(this.funcFlag).append("</FuncFlag>\n");
        sb.append("</Music>\n");
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusicUrl() {
        return this.musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getHqMusicUrl() {
        return this.hqMusicUrl;
    }

    public void setHqMusicUrl(String hqMusicUrl) {
        this.hqMusicUrl = hqMusicUrl;
    }

    public String getFuncFlag() {
        return this.funcFlag;
    }

    public void setFuncFlag(boolean funcFlag) {
        this.funcFlag = funcFlag ? "1" : "0";
    }
}
