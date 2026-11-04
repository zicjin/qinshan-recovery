/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpCheckoutDetailDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -4937963776731685631L;
    private String systemName;
    private String checkoutStatus;
    private String message;

    public String getSystemName() {
        return this.systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getCheckoutStatus() {
        return this.checkoutStatus;
    }

    public void setCheckoutStatus(String checkoutStatus) {
        this.checkoutStatus = checkoutStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
