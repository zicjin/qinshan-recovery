/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.media;

import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.media.MpNews;

public class MediaMpNews {
    private String agentid;
    private MpNews mpNews;
    private String media_id;

    public String getAgentid() {
        return this.agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public MpNews getMpNews() {
        return this.mpNews;
    }

    public void setMpNews(MpNews mpNews) {
        this.mpNews = mpNews;
    }

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }
}
