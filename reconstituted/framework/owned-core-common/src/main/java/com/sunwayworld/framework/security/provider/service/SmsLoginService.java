/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service;

public interface SmsLoginService {
    public void sendSmsCode(String var1, String var2);

    public void validSms(String var1, String var2);
}
