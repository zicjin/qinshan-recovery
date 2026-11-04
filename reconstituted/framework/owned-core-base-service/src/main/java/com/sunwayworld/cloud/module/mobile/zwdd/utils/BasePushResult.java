/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.utils;

import com.alibaba.fastjson.JSONObject;

public class BasePushResult {
    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;
    private int state;
    private String errorMsg;
    private String moreMsg;

    public BasePushResult() {
    }

    public BasePushResult(int state, String errorMsg, String moreMsg) {
        this.state = state;
        this.errorMsg = errorMsg;
        this.moreMsg = moreMsg;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMoreMsg() {
        return this.moreMsg;
    }

    public void setMoreMsg(String moreMsg) {
        this.moreMsg = moreMsg;
    }

    public String toString() {
        JSONObject obj = (JSONObject)JSONObject.toJSON(this);
        return obj.toJSONString();
    }
}
