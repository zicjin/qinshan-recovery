/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import java.io.Serializable;

public class ApiConfig
implements Serializable {
    private static final long serialVersionUID = 5243926308290263767L;
    private String token = null;
    private String corpId = null;
    private String corpSecret = null;
    private String agentId = null;
    private String encodingAesKey = null;
    private boolean messageEncrypt = false;

    public ApiConfig() {
    }

    public ApiConfig(String token) {
        this.setToken(token);
    }

    public ApiConfig(String token, String corpId) {
        this.setToken(token);
        this.setCorpId(corpId);
    }

    public ApiConfig(String token, String corpId, boolean messageEncrypt, String encodingAesKey) {
        this.setToken(token);
        this.setCorpId(corpId);
        this.setEncryptMessage(messageEncrypt);
        this.setEncodingAesKey(encodingAesKey);
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCorpId() {
        return this.corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCorpSecret() {
        return this.corpSecret;
    }

    public void setCorpSecret(String secret) {
        this.corpSecret = secret;
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
