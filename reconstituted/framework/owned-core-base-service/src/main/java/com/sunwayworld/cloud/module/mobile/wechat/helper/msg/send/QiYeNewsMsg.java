/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.MessageType;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.News;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeBaseMsg;

public class QiYeNewsMsg
extends QiYeBaseMsg {
    private int ArticleCount;
    private News news;

    public int getArticleCount() {
        return this.ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        this.ArticleCount = articleCount;
    }

    public QiYeNewsMsg(int articleCount, News news) {
        this.msgtype = MessageType.news.name();
        this.ArticleCount = articleCount;
        this.news = news;
    }

    public News getNews() {
        return this.news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public QiYeNewsMsg() {
        this.msgtype = MessageType.news.name();
    }
}
