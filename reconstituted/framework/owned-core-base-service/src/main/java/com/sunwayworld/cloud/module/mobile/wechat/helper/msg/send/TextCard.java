/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

public class TextCard {
    private String title;
    private String description;
    private String url;
    private String btntxt;

    public TextCard() {
    }

    public TextCard(String title, String description, String url, String btntxt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.btntxt = btntxt;
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBtntxt() {
        return this.btntxt;
    }

    public void setBtntxt(String btntxt) {
        this.btntxt = btntxt;
    }
}
