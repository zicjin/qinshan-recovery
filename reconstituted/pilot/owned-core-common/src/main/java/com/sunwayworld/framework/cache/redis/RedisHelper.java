package com.sunwayworld.framework.cache.redis;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.stereotype.Component;

@Component
public class RedisHelper {
   private static final String KEY_LINK_STRS = "::";
   private static RedisTemplate<String, Object> redisTemplate;

   @Autowired
   public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
      RedisHelper.redisTemplate = redisTemplate;
   }

   public static RedisTemplate<String, Object> getRedisTemplate() {
      if (redisTemplate == null) {
         redisTemplate = ApplicationContextHelper.getBean(RedisTemplate.class);
      }

      return redisTemplate;
   }

   public static boolean exists(String name, String key) {
      return getRedisTemplate().hasKey(getRedisKey(name, key));
   }

   public static <T> T get(String name, String key) {
      return (T)getRedisTemplate().opsForValue().get(getRedisKey(name, key));
   }

   public static <T> void put(String name, String key, T value) {
      getRedisTemplate().opsForValue().set(getRedisKey(name, key), value);
   }

   public static <T> boolean putIfAbsent(String name, String key, T value, int timeoutSeconds) {
      return !Boolean.FALSE.equals(getRedisTemplate().opsForValue().setIfAbsent(getRedisKey(name, key), value, timeoutSeconds, TimeUnit.SECONDS));
   }

   public static <T> void put(String name, String key, T value, int timeoutSeconds) {
      getRedisTemplate().opsForValue().set(getRedisKey(name, key), value, timeoutSeconds, TimeUnit.SECONDS);
   }

   public static <T> List<T> getList(String name, String key) {
      return (List<T>)getRedisTemplate().opsForList().range(getRedisKey(name, key), 0L, -1L);
   }

   public static <T> T getListFirstValue(String name, String key) {
      return (T)getRedisTemplate().opsForList().index(getRedisKey(name, key), 0L);
   }

   public static void removeListFirstValue(String name, String key) {
      getRedisTemplate().opsForList().leftPop(getRedisKey(name, key));
   }

   public static <T> void pushList(String name, String key, T item) {
      getRedisTemplate().opsForList().rightPush(getRedisKey(name, key), item);
   }

   public static <T> void pushListAll(String name, String key, List<T> itemList) {
      if (itemList.size() > 0) {
         getRedisTemplate().opsForList().rightPushAll(getRedisKey(name, key), itemList.toArray());
      }
   }

   public static <T> void pushList(String name, String key, T item, int timeoutSeconds) {
      multi(new SessionCallback<Object>() {
         @Override
         public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
            K redisKey = (K)RedisHelper.getRedisKey(name, key);
            operations.opsForList().rightPush(redisKey, (V)item);
            operations.expire(redisKey, timeoutSeconds, TimeUnit.SECONDS);
            return null;
         }
      });
   }

   public static void evict(String name, String key) {
      getRedisTemplate().opsForValue().getOperations().delete(getRedisKey(name, key));
   }

   public static void evict(String name) {
      evictCacheByPattern(name + "::" + "*");
   }

   public static void expire(String name, String key, int seconds) {
      getRedisTemplate().opsForValue().getOperations().expire(getRedisKey(name, key), seconds, TimeUnit.SECONDS);
   }

   public static <T> T multi(SessionCallback<T> callback) {
      return getRedisTemplate().execute(callback);
   }

   public static String getRedisKey(String cacheName, String cacheKey) {
      return cacheName + "::" + cacheKey;
   }

   public static <T> int getListSize(String name, String key) {
      return getRedisTemplate().opsForList().size(getRedisKey(name, key)).intValue();
   }

   public static <T> List<T> getList(String name, String key, int count) {
      int actualCount = getListSize(name, key);
      if (actualCount <= 0) {
         return CollectionUtils.emptyList();
      } else {
         int requestCount = Math.min(count, actualCount);
         return (List<T>)getRedisTemplate().opsForList().range(getRedisKey(name, key), 0L, requestCount - 1);
      }
   }

   public static void removeList(String name, String key, int count) {
      getRedisTemplate().opsForList().trim(getRedisKey(name, key), count, -1L);
   }

   private static void evictCacheByPattern(String pattern) {
      getRedisTemplate().execute((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
         Set<String> keySet = new HashSet<>();
         Cursor<byte[]> cursor = connection.scan(new ScanOptionsBuilder().match(pattern).build());

         while (cursor.hasNext()) {
            keySet.add(new String(cursor.next()));
            if (keySet.size() > 100) {
               redisTemplate.delete(keySet);
               keySet.clear();
            }
         }

         if (!keySet.isEmpty()) {
            redisTemplate.delete(keySet);
         }

         return null;
      });
   }
}
