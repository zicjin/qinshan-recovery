/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean.qyweixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.qyweixin.ReturnCode;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import java.io.Serializable;
import java.util.Map;

public class AccessToken
implements RetryUtils.ResultCheck,
Serializable {
    private static final long serialVersionUID = -822464425433824314L;
    private String access_token;
    private Integer expires_in;
    private Integer errcode;
    private String errmsg;
    private Long expiredTime;
    private String json;

    public AccessToken(String jsonStr) {
        this.json = jsonStr;
        try {
            Map temp = JSONObject.parseObject(jsonStr, Map.class);
            this.access_token = (String)temp.get("access_token");
            this.expires_in = (Integer)temp.get("expires_in");
            this.errcode = (Integer)temp.get("errcode");
            this.errmsg = (String)temp.get("errmsg");
            if (this.expires_in != null) {
                this.expiredTime = System.currentTimeMillis() + (long)((this.expires_in - 9) * 1000);
            }
            if (temp.containsKey("expiredTime")) {
                this.expiredTime = (Long)temp.get("expiredTime");
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

    public String getCacheJson() {
        Map temp = JSONObject.parseObject(this.json, Map.class);
        temp.put("expiredTime", this.expiredTime);
        temp.remove("expires_in");
        return JSON.toJSONString(temp);
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

    public Long getExpiredTime() {
        return this.expiredTime;
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

    @Override
    public boolean matching() {
        return this.isAvailable();
    }
}
