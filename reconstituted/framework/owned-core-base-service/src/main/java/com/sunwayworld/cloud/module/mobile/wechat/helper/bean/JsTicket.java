/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.weixin.ReturnCode;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import java.io.Serializable;
import java.util.Map;

public class JsTicket
implements RetryUtils.ResultCheck,
Serializable {
    private static final long serialVersionUID = 6600179487477942329L;
    private String ticket;
    private Integer expires_in;
    private Integer errcode;
    private String errmsg;
    private Long expiredTime;
    private String json;

    public JsTicket(String jsonStr) {
        this.json = jsonStr;
        try {
            Map temp = JSONObject.parseObject(jsonStr, Map.class);
            this.ticket = (String)temp.get("ticket");
            this.expires_in = (Integer)temp.get("expires_in");
            this.errcode = (Integer)temp.get("errcode");
            this.errmsg = (String)temp.get("errmsg");
            if (this.expires_in != null) {
                this.expiredTime = System.currentTimeMillis() + (long)((this.expires_in - 5) * 1000);
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
        if (!this.isSucceed()) {
            return false;
        }
        if (this.expiredTime < System.currentTimeMillis()) {
            return false;
        }
        return this.ticket != null;
    }

    public String getTicket() {
        return this.ticket;
    }

    public Integer getExpiresIn() {
        return this.expires_in;
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

    public Long getExpiredTime() {
        return this.expiredTime;
    }

    public boolean isSucceed() {
        Integer errorCode = this.getErrorCode();
        return errorCode == null || errorCode == 0;
    }

    @Override
    public boolean matching() {
        return this.isAvailable();
    }
}
