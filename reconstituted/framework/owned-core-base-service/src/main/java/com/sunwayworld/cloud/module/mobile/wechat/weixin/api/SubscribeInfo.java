/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.Data;
import com.sunwayworld.framework.utils.StringUtils;

class SubscribeInfo {
    private String touser;
    private String template_id;
    private String url;
    private String scene;
    private String title;
    private Data data;

    public static SubscribeInfo Builder() {
        return new SubscribeInfo();
    }

    public SubscribeInfo build() {
        if (StringUtils.isBlank(this.touser)) {
            throw new IllegalStateException("touser is null");
        }
        if (StringUtils.isBlank(this.template_id)) {
            throw new IllegalStateException("template_id is null");
        }
        if (StringUtils.isBlank(this.url)) {
            throw new IllegalStateException("url is null");
        }
        if (StringUtils.isBlank(this.scene)) {
            throw new IllegalStateException("scene is null");
        }
        if (StringUtils.isBlank(this.title)) {
            throw new IllegalStateException("title is null");
        }
        if (this.data == null) {
            throw new IllegalStateException("data is null");
        }
        return new SubscribeInfo(this.touser, this.template_id, this.url, this.scene, this.title, this.data);
    }

    private SubscribeInfo() {
    }

    private SubscribeInfo(String touser, String template_id, String url, String scene, String title, Data data) {
        this.touser = touser;
        this.template_id = template_id;
        this.url = url;
        this.scene = scene;
        this.title = title;
        this.data = data;
    }

    public String getTouser() {
        return this.touser;
    }

    public SubscribeInfo setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public SubscribeInfo setTemplate_id(String template_id) {
        this.template_id = template_id;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public SubscribeInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getScene() {
        return this.scene;
    }

    public SubscribeInfo setScene(String scene) {
        this.scene = scene;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public SubscribeInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Data getData() {
        return this.data;
    }

    public SubscribeInfo setData(Data data) {
        this.data = data;
        return this;
    }
}
