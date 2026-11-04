/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import java.io.Serializable;

public class QrCodeLoginDTO
implements Serializable {
    private static final long serialVersionUID = 808811091757545653L;
    private String qrToken;
    private String loginStatusUrl;
    private String scanUrl;

    public String getQrToken() {
        return this.qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getLoginStatusUrl() {
        return this.loginStatusUrl;
    }

    public void setLoginStatusUrl(String loginStatusUrl) {
        this.loginStatusUrl = loginStatusUrl;
    }

    public String getScanUrl() {
        return this.scanUrl;
    }

    public void setScanUrl(String scanUrl) {
        this.scanUrl = scanUrl;
    }
}
