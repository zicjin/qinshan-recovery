/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;

public class QrCodeLoginStatusDTO
implements Serializable {
    private static final long serialVersionUID = -1286788514929197549L;
    private String status;
    private String userId;
    private String userName;
    private String loginSuccessUrl;
    @JSONField(serialize=false)
    private String scanSuccessKey;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginSuccessUrl() {
        return this.loginSuccessUrl;
    }

    public void setLoginSuccessUrl(String loginSuccessUrl) {
        this.loginSuccessUrl = loginSuccessUrl;
    }

    public String getScanSuccessKey() {
        return this.scanSuccessKey;
    }

    public void setScanSuccessKey(String scanSuccessKey) {
        this.scanSuccessKey = scanSuccessKey;
    }
}
