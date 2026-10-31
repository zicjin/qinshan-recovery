package com.sunwayworld.framework;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import java.util.concurrent.atomic.AtomicInteger;

public final class MemoryCacheManagerContractTest {
    public static void main(String[] args) {
        String key = "bootstrap-test";
        MemoryCacheManager.remove(key);
        if (MemoryCacheManager.isPresent(key) || MemoryCacheManager.get(key) != null) throw new AssertionError("empty cache");
        AtomicInteger calls = new AtomicInteger();
        Object value = MemoryCacheManager.get(key, () -> { calls.incrementAndGet(); return "value"; });
        if (!"value".equals(value) || calls.get() != 1 || !MemoryCacheManager.isPresent(key)) throw new AssertionError("supplier cache");
        if (!"value".equals(MemoryCacheManager.get(key, () -> { calls.incrementAndGet(); return "other"; })) || calls.get() != 1) throw new AssertionError("cache hit");
        MemoryCacheManager.remove(key);
        MemoryCacheManager.get(key, () -> null);
        if (!MemoryCacheManager.isPresent(key) || MemoryCacheManager.get(key) == null) throw new AssertionError("null sentinel");
        MemoryCacheManager.remove(key);
        if (MemoryCacheManager.isPresent(key)) throw new AssertionError("remove");
        System.out.println("memory cache contract: PASS");
    }
}
