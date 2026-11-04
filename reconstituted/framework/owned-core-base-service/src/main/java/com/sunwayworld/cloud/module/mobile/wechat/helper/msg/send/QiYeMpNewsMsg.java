/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MpArticle;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;
import java.util.List;

public class QiYeMpNewsMsg
extends QiYeBaseMsg {
    private int mpArticleCount;
    private List<MpArticle> articles;

    public int getMpArticleCount() {
        return this.mpArticleCount;
    }

    public void setMpArticleCount(int mpArticleCount) {
        this.mpArticleCount = mpArticleCount;
    }

    public List<MpArticle> getArticles() {
        return this.articles;
    }

    public void setArticles(List<MpArticle> articles) {
        this.articles = articles;
    }

    public QiYeMpNewsMsg() {
        this.msgtype = MessageType.mpnews.name();
    }
}
