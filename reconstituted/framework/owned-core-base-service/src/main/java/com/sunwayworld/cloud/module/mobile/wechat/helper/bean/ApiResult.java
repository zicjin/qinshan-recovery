/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.weixin.ReturnCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class ApiResult
implements Serializable {
    private static final long serialVersionUID = 722417391137943513L;
    private Map<String, Object> attrs;
    private String json;

    public ApiResult(String jsonStr) {
        this.json = jsonStr;
        try {
            Map temp;
            this.attrs = temp = JSONObject.parseObject(jsonStr, Map.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ApiResult create(String jsonStr) {
        return new ApiResult(jsonStr);
    }

    public String getJson() {
        return this.json;
    }

    public String toString() {
        return this.getJson();
    }

    public boolean isSucceed() {
        Integer errorCode = this.getErrorCode();
        return errorCode == null || errorCode == 0;
    }

    public Integer getErrorCode() {
        return this.getInt("errcode");
    }

    public String getErrorMsg() {
        String result;
        Integer errorCode = this.getErrorCode();
        if (errorCode != null && (result = ReturnCode.get(errorCode)) != null) {
            return result;
        }
        return (String)this.attrs.get("errmsg");
    }

    public <T> T get(String name) {
        return (T)this.attrs.get(name);
    }

    public String getStr(String name) {
        return (String)this.attrs.get(name);
    }

    public Integer getInt(String name) {
        Number number = (Number)this.attrs.get(name);
        return number == null ? null : Integer.valueOf(number.intValue());
    }

    public Long getLong(String name) {
        Number number = (Number)this.attrs.get(name);
        return number == null ? null : Long.valueOf(number.longValue());
    }

    public BigInteger getBigInteger(String name) {
        return (BigInteger)this.attrs.get(name);
    }

    public Double getDouble(String name) {
        return (Double)this.attrs.get(name);
    }

    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal)this.attrs.get(name);
    }

    public Boolean getBoolean(String name) {
        return (Boolean)this.attrs.get(name);
    }

    public List getList(String name) {
        return (List)this.attrs.get(name);
    }

    public Map getMap(String name) {
        return (Map)this.attrs.get(name);
    }

    public Map<String, Object> getAttrs() {
        return this.attrs;
    }

    public boolean isAccessTokenInvalid() {
        Integer ec = this.getErrorCode();
        return ec != null && (ec == 40001 || ec == 42001 || ec == 42002 || ec == 40014);
    }
}
