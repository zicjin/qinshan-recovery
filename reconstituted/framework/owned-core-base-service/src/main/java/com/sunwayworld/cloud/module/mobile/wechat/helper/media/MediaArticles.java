/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.media;

import java.io.Serializable;

public class MediaArticles
implements Serializable {
    private static final long serialVersionUID = -4053285499598013940L;
    private String title;
    private String thumb_media_id;
    private String author;
    private String digest;
    private boolean show_cover_pic;
    private String content;
    private String content_source_url;
    private Integer need_open_comment;
    private Integer only_fans_can_comment;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb_media_id() {
        return this.thumb_media_id;
    }

    public void setThumb_media_id(String thumb_media_id) {
        this.thumb_media_id = thumb_media_id;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDigest() {
        return this.digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getShow_cover_pic() {
        return this.show_cover_pic ? 1 : 0;
    }

    public void setShow_cover_pic(boolean show_cover_pic) {
        this.show_cover_pic = show_cover_pic;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent_source_url() {
        return this.content_source_url;
    }

    public void setContent_source_url(String content_source_url) {
        this.content_source_url = content_source_url;
    }

    public Integer getNeed_open_comment() {
        return this.need_open_comment;
    }

    public void setNeed_open_comment(Integer need_open_comment) {
        this.need_open_comment = need_open_comment;
    }

    public Integer getOnly_fans_can_comment() {
        return this.only_fans_can_comment;
    }

    public void setOnly_fans_can_comment(Integer only_fans_can_comment) {
        this.only_fans_can_comment = only_fans_can_comment;
    }
}
