/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.cache;

import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.framework.cache.redis.RedisHelper;

public class RedisAccessTokenCache
implements IAccessTokenCache {
    private final String ACCESS_TOKEN_PREFIX = "GIKAM_WEIXIN_TOKEN";

    @Override
    public String get(String key) {
        return (String)RedisHelper.get("GIKAM_WEIXIN_TOKEN", key);
    }

    @Override
    public void set(String key, String jsonValue) {
        RedisHelper.putIfAbsent("GIKAM_WEIXIN_TOKEN", key, jsonValue, 7191);
    }

    @Override
    public void remove(String key) {
        RedisHelper.evict("GIKAM_WEIXIN_TOKEN", key);
    }
}
