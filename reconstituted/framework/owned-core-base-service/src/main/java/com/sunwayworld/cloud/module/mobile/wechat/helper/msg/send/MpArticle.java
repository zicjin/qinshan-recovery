/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

public class MpArticle {
    private String thumb_media_id;
    private String author;
    private String content_source_url;
    private String title;
    private String content;
    private String digest;
    private int show_cover_pic;

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumb_media_id() {
        return this.thumb_media_id;
    }

    public void setThumb_media_id(String thumbMediaId) {
        this.thumb_media_id = thumbMediaId;
    }

    public String getContent_source_url() {
        return this.content_source_url;
    }

    public void setContent_source_url(String contentSourceUrl) {
        this.content_source_url = contentSourceUrl;
    }

    public int getShow_cover_pic() {
        return this.show_cover_pic;
    }

    public void setShow_cover_pic(int showCoverPic) {
        this.show_cover_pic = showCoverPic;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return this.digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
