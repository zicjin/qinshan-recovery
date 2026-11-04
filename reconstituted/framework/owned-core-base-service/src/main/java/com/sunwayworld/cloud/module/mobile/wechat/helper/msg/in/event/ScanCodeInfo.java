/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import java.io.Serializable;

public class ScanCodeInfo
implements Serializable {
    private String ScanType;
    private String ScanResult;

    public ScanCodeInfo(String scanType, String scanResult) {
        this.ScanType = scanType;
        this.ScanResult = scanResult;
    }

    public String getScanType() {
        return this.ScanType;
    }

    public void setScanType(String scanType) {
        this.ScanType = scanType;
    }

    public String getScanResult() {
        return this.ScanResult;
    }

    public void setScanResult(String scanResult) {
        this.ScanResult = scanResult;
    }
}
