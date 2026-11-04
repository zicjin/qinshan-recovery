/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import java.io.Serializable;

public class QrCodeScanStatusDTO
implements Serializable {
    private static final long serialVersionUID = 451426778722573330L;
    private String status;
    private String confirmUrl;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmUrl() {
        return this.confirmUrl;
    }

    public void setConfirmUrl(String confirmUrl) {
        this.confirmUrl = confirmUrl;
    }
}
