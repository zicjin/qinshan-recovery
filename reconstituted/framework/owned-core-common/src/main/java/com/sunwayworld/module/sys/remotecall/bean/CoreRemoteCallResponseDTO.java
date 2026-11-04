/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.bean;

import java.io.Serializable;

public class CoreRemoteCallResponseDTO
implements Serializable {
    private static final long serialVersionUID = -1437799989027172801L;
    private String responseStatus;
    private String responseData;

    public String getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseData() {
        return this.responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}
