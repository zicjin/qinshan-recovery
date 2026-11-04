/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api;

import java.io.Serializable;

public class ApiConfig
implements Serializable {
    private static final long serialVersionUID = 5243926308290263767L;
    private String token = null;
    private String appId = null;
    private String appSecret = null;
    private String encodingAesKey = null;
    private boolean messageEncrypt = false;

    public ApiConfig() {
    }

    public ApiConfig(String token) {
        this.setToken(token);
    }

    public ApiConfig(String token, String appId, String appSecret) {
        this.setToken(token);
        this.setAppId(appId);
        this.setAppSecret(appSecret);
    }

    public ApiConfig(String token, String appId, String appSecret, boolean messageEncrypt, String encodingAesKey) {
        this.setToken(token);
        this.setAppId(appId);
        this.setAppSecret(appSecret);
        this.setEncryptMessage(messageEncrypt);
        this.setEncodingAesKey(encodingAesKey);
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getEncodingAesKey() {
        return this.encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    public boolean isEncryptMessage() {
        return this.messageEncrypt;
    }

    public void setEncryptMessage(boolean messageEncrypt) {
        this.messageEncrypt = messageEncrypt;
    }
}
