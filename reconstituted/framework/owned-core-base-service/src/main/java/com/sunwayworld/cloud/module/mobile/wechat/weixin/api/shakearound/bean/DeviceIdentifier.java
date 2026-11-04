/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound.bean;

public class DeviceIdentifier {
    private Integer device_id;
    private String uuid;
    private Integer major;
    private Integer minor;

    public Integer getDevice_id() {
        return this.device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getMajor() {
        return this.major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return this.minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }
}
