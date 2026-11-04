/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreImMessageDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -28195856334012968L;
    private String type;
    private String data;
    private String receiveUserId;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getReceiveUserId() {
        return this.receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }
}
