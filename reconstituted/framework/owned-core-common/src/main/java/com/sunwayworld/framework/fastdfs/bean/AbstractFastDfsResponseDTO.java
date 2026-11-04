/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.bean;

import java.io.Serializable;

public abstract class AbstractFastDfsResponseDTO
implements Serializable {
    private static final long serialVersionUID = -1328106920767173266L;
    private String message;
    private String status;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean success() {
        return "ok".equals(this.getStatus());
    }
}
