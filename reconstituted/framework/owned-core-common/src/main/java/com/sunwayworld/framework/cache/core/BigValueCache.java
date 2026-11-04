/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.core;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class BigValueCache<T> {
    private final String cacheName;
    private final String cacheKey;
    private final Supplier<T> supplier;
    private final Function<T, String> cacheKeyFunction;
    private String cacheFlagValue;
    private T cache;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public BigValueCache(String cacheName, String cacheKey, Supplier<T> supplier, Function<T, String> cacheKeyFunction) {
        this.cacheName = cacheName;
        this.cacheKey = cacheKey;
        this.supplier = supplier;
        this.cacheKeyFunction = cacheKeyFunction;
        this.cache = supplier.get();
        this.cacheFlagValue = cacheKeyFunction.apply(this.cache);
        RedisHelper.put(cacheName, cacheKey, this.cacheFlagValue);
    }

    public String getIdentity() {
        return this.cacheName + "$" + this.cacheKey;
    }

    public T getCacheValue() {
        if (!Objects.equals(this.cacheFlagValue, RedisHelper.get(this.cacheName, this.cacheKey))) {
            this.reload();
        }
        return this.cache;
    }

    public void reload() {
        if (this.atomicInteger.getAndAdd(1) > 0) {
            return;
        }
        try {
            T tempCache = this.supplier.get();
            String tempCacheFlagValue = this.cacheKeyFunction.apply(this.cache);
            RedisHelper.put(this.cacheName, this.cacheKey, tempCacheFlagValue);
            this.cache = tempCache;
            this.cacheFlagValue = tempCacheFlagValue;
        }
        finally {
            if (this.atomicInteger.decrementAndGet() > 0) {
                this.atomicInteger.set(0);
                this.reload();
            }
        }
    }
}
