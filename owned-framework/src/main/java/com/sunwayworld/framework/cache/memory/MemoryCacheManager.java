package com.sunwayworld.framework.cache.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/** Owned plain-Java cache seam used by the observed SunwayApplication bootstrap. */
public class MemoryCacheManager {
    private static final Object EMPTY = new Object();
    private static final ConcurrentMap<String, Object> CACHE = new ConcurrentHashMap<String, Object>();
    public MemoryCacheManager() { }
    public static void put(String key, Object value) { CACHE.put(key, value); }
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) { return (T)CACHE.get(key); }
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Supplier<T> supplier) {
        Object value = CACHE.get(key);
        if (value == EMPTY) return null;
        if (value != null) return (T)value;
        Object created = supplier.get();
        CACHE.put(key, created == null ? EMPTY : created);
        value = created == null ? EMPTY : created;
        return value == EMPTY ? null : (T)value;
    }
    public static boolean isPresent(String key) { return CACHE.containsKey(key); }
    public static void remove(String key) { CACHE.remove(key); }
}
