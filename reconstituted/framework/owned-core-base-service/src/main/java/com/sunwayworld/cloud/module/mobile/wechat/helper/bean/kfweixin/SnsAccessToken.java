/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin.ReturnCode;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import java.io.Serializable;
import java.util.Map;

public class SnsAccessToken
implements RetryUtils.ResultCheck,
Serializable {
    private static final long serialVersionUID = 6369625123403343963L;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    private Integer errcode;
    private String errmsg;
    private Long expiredTime;
    private String json;

    public SnsAccessToken(String jsonStr) {
        this.json = jsonStr;
        try {
            Map temp = JSONObject.parseObject(jsonStr, Map.class);
            this.access_token = (String)temp.get("access_token");
            this.expires_in = this.getInt(temp, "expires_in");
            this.refresh_token = (String)temp.get("refresh_token");
            this.openid = (String)temp.get("openid");
            this.unionid = (String)temp.get("unionid");
            this.scope = (String)temp.get("scope");
            this.errcode = this.getInt(temp, "errcode");
            this.errmsg = (String)temp.get("errmsg");
            if (this.expires_in != null) {
                this.expiredTime = System.currentTimeMillis() + (long)((this.expires_in - 5) * 1000);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getJson() {
        return this.json;
    }

    private Integer getInt(Map<String, Object> temp, String key) {
        Number number = (Number)temp.get(key);
        return number == null ? null : Integer.valueOf(number.intValue());
    }

    public boolean isAvailable() {
        if (this.expiredTime == null) {
            return false;
        }
        if (this.errcode != null) {
            return false;
        }
        if (this.expiredTime < System.currentTimeMillis()) {
            return false;
        }
        return this.access_token != null;
    }

    public String getAccessToken() {
        return this.access_token;
    }

    public Integer getExpiresIn() {
        return this.expires_in;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public String getOpenid() {
        return this.openid;
    }

    public String getScope() {
        return this.scope;
    }

    public Integer getErrorCode() {
        return this.errcode;
    }

    public String getErrorMsg() {
        String result;
        if (this.errcode != null && (result = ReturnCode.get(this.errcode)) != null) {
            return result;
        }
        return this.errmsg;
    }

    public String getUnionid() {
        return this.unionid;
    }

    @Override
    public boolean matching() {
        return this.isAvailable();
    }
}
