/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.cache.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class MemoryCacheManager {
    private static final Object EMPTY = new Object();
    private static final ConcurrentMap<String, Object> CACHE = new ConcurrentHashMap<String, Object>();

    public static void put(String key, Object value) {
        CACHE.put(key, value);
    }

    public static <T> T get(String key) {
        return (T)CACHE.get(key);
    }

    public static <T> T get(String key, Supplier<T> supplier) {
        Object value = CACHE.get(key);
        if (EMPTY == value) {
            return null;
        }
        if (value == null) {
            value = supplier.get();
            if (value == null) {
                CACHE.put(key, EMPTY);
            } else {
                CACHE.put(key, value);
            }
        }
        return (T)value;
    }

    public static boolean isPresent(String key) {
        return CACHE.containsKey(key);
    }

    public static void remove(String key) {
        CACHE.remove(key);
    }
}
