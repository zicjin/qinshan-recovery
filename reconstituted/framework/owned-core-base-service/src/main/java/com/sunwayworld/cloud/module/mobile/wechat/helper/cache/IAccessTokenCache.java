/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.cache;

public interface IAccessTokenCache {
    public static final int DEFAULT_TIME_OUT = 7191;

    public String get(String var1);

    public void set(String var1, String var2);

    public void remove(String var1);
}
