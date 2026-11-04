/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

public class PaymentException
extends Exception {
    private static final long serialVersionUID = 6615472990468215919L;
    private final String returnCode;
    private final String returnMsg;

    public PaymentException(String returnCode, String returnMsg) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public String getReturnCode() {
        return this.returnCode;
    }

    public String getReturnMsg() {
        return this.returnMsg;
    }
}
