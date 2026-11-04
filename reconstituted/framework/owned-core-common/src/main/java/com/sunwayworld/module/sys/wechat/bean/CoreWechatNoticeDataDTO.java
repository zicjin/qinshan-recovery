/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.bean;

import java.io.Serializable;

public class CoreWechatNoticeDataDTO
implements Serializable {
    private static final long serialVersionUID = -1589427411172448943L;
    private String value;
    private String color;

    public CoreWechatNoticeDataDTO(String value) {
        this.value = value;
        this.color = "#173177";
    }

    public CoreWechatNoticeDataDTO(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
