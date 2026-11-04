/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service;

public interface SecurityProviderService {
    public static final String SECURITY_RSA_KEY = "SECURITY-RSA-KEY";

    public String getPublicKey();

    public String getPrivateKey(String var1);

    public void preLogout(String var1);
}
