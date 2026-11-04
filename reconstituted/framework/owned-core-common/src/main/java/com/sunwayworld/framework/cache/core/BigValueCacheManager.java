package com.sunwayworld.framework.cache.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BigValueCacheManager {
   private static Map<String, BigValueCache<?>> cacheContainer = new ConcurrentHashMap<>();

   public static void addCache(BigValueCache<?> cache) {
      cacheContainer.put(cache.getIdentity(), cache);
   }

   public static <T> BigValueCache<T> getCache(String cacheName, String cacheValue) {
      return (BigValueCache<T>)cacheContainer.get(cacheName + "$" + cacheValue);
   }

   public static <T> void reload(String cacheName, String cacheValue) {
      BigValueCache<T> cache = getCache(cacheName, cacheValue);
      if (cache != null) {
         cache.reload();
      }
   }
}
