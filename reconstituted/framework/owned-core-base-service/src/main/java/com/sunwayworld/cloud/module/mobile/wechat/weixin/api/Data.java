/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.Content;

class Data {
    private Content content;

    public static Data Builder() {
        return new Data();
    }

    public Data build() {
        if (this.content == null) {
            throw new IllegalStateException("content is null");
        }
        return new Data(this.content);
    }

    private Data() {
    }

    private Data(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return this.content;
    }

    public Data setContent(Content content) {
        this.content = content;
        return this;
    }
}
