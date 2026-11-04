/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class DeviceAuth
implements Serializable {
    private static final long serialVersionUID = -3535838682817586669L;
    private String id;
    private String mac;
    @JsonProperty(value="connect_protocol")
    @JSONField(name="connect_protocol")
    private String connectProtocol;
    @JsonProperty(value="auth_key")
    @JSONField(name="auth_key")
    private String authKey;
    @JsonProperty(value="close_strategy")
    @JSONField(name="close_strategy")
    private String closeStrategy;
    @JsonProperty(value="conn_strategy")
    @JSONField(name="conn_strategy")
    private String connStrategy;
    @JsonProperty(value="crypt_method")
    @JSONField(name="crypt_method")
    private String cryptMethod = "0";
    @JsonProperty(value="auth_ver")
    @JSONField(name="auth_ver")
    private String authVer;
    @JsonProperty(value="manu_mac_pos")
    @JSONField(name="manu_mac_pos")
    private String manuMacPos;
    @JsonProperty(value="ser_mac_pos")
    @JSONField(name="ser_mac_pos")
    private String serMacPos;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getConnectProtocol() {
        return this.connectProtocol;
    }

    public void setConnectProtocol(String connectProtocol) {
        this.connectProtocol = connectProtocol;
    }

    public String getAuthKey() {
        return this.authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getCloseStrategy() {
        return this.closeStrategy;
    }

    public void setCloseStrategy(String closeStrategy) {
        this.closeStrategy = closeStrategy;
    }

    public String getConnStrategy() {
        return this.connStrategy;
    }

    public void setConnStrategy(String connStrategy) {
        this.connStrategy = connStrategy;
    }

    public String getCryptMethod() {
        return this.cryptMethod;
    }

    public void setCryptMethod(String cryptMethod) {
        this.cryptMethod = cryptMethod;
    }

    public String getAuthVer() {
        return this.authVer;
    }

    public void setAuthVer(String authVer) {
        this.authVer = authVer;
    }

    public String getManuMacPos() {
        return this.manuMacPos;
    }

    public void setManuMacPos(String manuMacPos) {
        this.manuMacPos = manuMacPos;
    }

    public String getSerMacPos() {
        return this.serMacPos;
    }

    public void setSerMacPos(String serMacPos) {
        this.serMacPos = serMacPos;
    }
}
