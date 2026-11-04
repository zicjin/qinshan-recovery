/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.cache;

import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultAccessTokenCache
implements IAccessTokenCache {
    private Map<String, String> map = new ConcurrentHashMap<String, String>();

    @Override
    public String get(String key) {
        return this.map.get(key);
    }

    @Override
    public void set(String key, String jsonValue) {
        this.map.put(key, jsonValue);
    }

    @Override
    public void remove(String key) {
        this.map.remove(key);
    }
}
