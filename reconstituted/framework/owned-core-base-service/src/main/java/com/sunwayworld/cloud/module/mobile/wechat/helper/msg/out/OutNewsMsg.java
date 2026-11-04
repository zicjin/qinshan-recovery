/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.News;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out.OutMsg;
import java.util.ArrayList;
import java.util.List;

public class OutNewsMsg
extends OutMsg {
    private List<News> articles = new ArrayList<News>();

    public OutNewsMsg() {
        this.msgType = "news";
    }

    public OutNewsMsg(InMsg inMsg) {
        super(inMsg);
        this.msgType = "news";
    }

    @Override
    protected void subXml(StringBuilder sb) {
        sb.append("<ArticleCount>").append(this.getArticleCount()).append("</ArticleCount>\n");
        sb.append("<Articles>\n");
        for (News x : this.articles) {
            sb.append("<item>\n");
            sb.append("<Title><![CDATA[").append(this.nullToBlank(x.getTitle())).append("]]></Title>\n");
            sb.append("<Description><![CDATA[").append(this.nullToBlank(x.getDescription())).append("]]></Description>\n");
            sb.append("<PicUrl><![CDATA[").append(this.nullToBlank(x.getPicUrl())).append("]]></PicUrl>\n");
            sb.append("<Url><![CDATA[").append(this.nullToBlank(x.getUrl())).append("]]></Url>\n");
            sb.append("</item>\n");
        }
        sb.append("</Articles>\n");
    }

    public Integer getArticleCount() {
        return this.articles.size();
    }

    public List<News> getArticles() {
        return this.articles;
    }

    public void setArticles(List<News> articles) {
        if (articles != null) {
            this.articles = articles;
        }
    }

    public OutNewsMsg addNews(List<News> articles) {
        if (articles != null) {
            this.articles.addAll(articles);
        }
        return this;
    }

    public OutNewsMsg addNews(String title, String description, String picUrl, String url) {
        this.articles.add(new News(title, description, picUrl, url));
        return this;
    }

    public OutNewsMsg addNews(News news) {
        this.articles.add(news);
        return this;
    }
}
