/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.framework.utils.StringUtils;

class Content {
    private String value;
    private String color;

    public static Content Builder() {
        return new Content();
    }

    public Content build() {
        if (StringUtils.isBlank(this.value)) {
            throw new IllegalStateException("value is null");
        }
        if (StringUtils.isBlank(this.color)) {
            throw new IllegalStateException("color is null");
        }
        return new Content(this.value, this.color);
    }

    private Content() {
    }

    private Content(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public Content setValue(String value) {
        this.value = value;
        return this;
    }

    public String getColor() {
        return this.color;
    }

    public Content setColor(String color) {
        this.color = color;
        return this;
    }
}
