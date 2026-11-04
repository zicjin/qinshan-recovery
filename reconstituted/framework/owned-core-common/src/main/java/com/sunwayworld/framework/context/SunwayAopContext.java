/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context;

import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SunwayAopContext {
    private static final ThreadLocal<Map<String, Object>> PROXY_CACHE = new ThreadLocal();
    private static final ThreadLocal<Object> CURRENT_PROXY = new ThreadLocal();

    public static <T> T currentProxy() {
        return (T)CURRENT_PROXY.get();
    }

    public static void setCurrentProxy(Object proxy) {
        if (proxy == null) {
            CURRENT_PROXY.remove();
        } else {
            CURRENT_PROXY.set(proxy);
        }
    }

    public static boolean initCache() {
        if (PROXY_CACHE.get() != null) {
            return false;
        }
        PROXY_CACHE.set(new HashMap());
        return true;
    }

    public static void clearCache() {
        PROXY_CACHE.remove();
    }

    public static void removeCache(String key) {
        PROXY_CACHE.get().remove(key);
    }

    public static void putCache(String key, Object value) {
        PROXY_CACHE.get().put(key, value);
    }

    public static <T> T getCache(String key) {
        Map<String, Object> map = PROXY_CACHE.get();
        if (map == null) {
            return null;
        }
        return (T)map.get(key);
    }

    public static <T> T getCache(String key, Supplier<T> supplier) {
        T item = SunwayAopContext.getCache(key);
        if (item == null) {
            item = supplier.get();
            SunwayAopContext.putCache(key, item);
        }
        return item;
    }

    public static void clearCurrentCache() {
        Map<String, Object> map = PROXY_CACHE.get();
        if (map == null) {
            return;
        }
        map.keySet().removeIf(k -> StringUtils.startsWith(k, SunwayAopContext.getCurrentCacheKeyPrefix()));
    }

    public static void removeCurrentCache(String key) {
        PROXY_CACHE.get().remove(SunwayAopContext.getCurrentCacheKeyPrefix() + key);
    }

    public static void putCurrentCache(String key, Object value) {
        PROXY_CACHE.get().put(SunwayAopContext.getCurrentCacheKeyPrefix() + key, value);
    }

    public static <T> T getCurrentCache(String key) {
        Map<String, Object> map = PROXY_CACHE.get();
        if (map == null) {
            return null;
        }
        return (T)map.get(SunwayAopContext.getCurrentCacheKeyPrefix() + key);
    }

    public static <T> T getCurrentCache(String key, Supplier<T> supplier) {
        T item = SunwayAopContext.getCache(SunwayAopContext.getCurrentCacheKeyPrefix() + key);
        if (item == null) {
            item = supplier.get();
            SunwayAopContext.putCache(SunwayAopContext.getCurrentCacheKeyPrefix() + key, item);
        }
        return item;
    }

    private static String getCurrentCacheKeyPrefix() {
        return SunwayAopContext.currentProxy() + "$";
    }
}
