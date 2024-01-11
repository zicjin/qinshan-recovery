package com.sunwayworld.emc.framework.sso;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author zhengxy@sunwayworld.com 2023年11月27日
 */
public class IdaasAccessTokenResponse implements Serializable {

    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "expires_in")
    private int expiresIn;
    @JSONField(name = "token_type")
    private String tokenType;
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
