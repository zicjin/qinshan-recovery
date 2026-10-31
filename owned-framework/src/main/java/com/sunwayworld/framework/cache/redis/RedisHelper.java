package com.sunwayworld.framework.cache.redis;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Owned implementation of the recovered RedisHelper public contract.
 *
 * The helper deliberately delegates serialization and connection lifecycle to
 * the injected Spring Data RedisTemplate, matching the recovered application
 * boundary without retaining a supplier runtime dependency.
 */
public class RedisHelper {
    private static final String KEY_LINK_STRS = "::";
    private static RedisTemplate<String, Object> redisTemplate;

    public RedisHelper() {}

    public void setRedisTemplate(RedisTemplate<String, Object> template) {
        redisTemplate = template;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = OwnedApplicationContext.getBean("redisTemplate", RedisTemplate.class);
            if (redisTemplate == null) redisTemplate = OwnedApplicationContext.getBean(RedisTemplate.class);
            if (redisTemplate == null) throw new IllegalStateException("application context is not available");
        }
        return redisTemplate;
    }

    public static boolean exists(String name, String key) {
        return getRedisTemplate().hasKey(getRedisKey(name, key)).booleanValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String name, String key) {
        ValueOperations<String, Object> values = getRedisTemplate().opsForValue();
        return (T) values.get(getRedisKey(name, key));
    }

    public static <T> void put(String name, String key, T item) {
        getRedisTemplate().opsForValue().set(getRedisKey(name, key), item);
    }

    public static <T> boolean putIfAbsent(String name, String key, T item, int timeoutSeconds) {
        Boolean result = getRedisTemplate().opsForValue().setIfAbsent(getRedisKey(name, key), item, timeoutSeconds, TimeUnit.SECONDS);
        return !Boolean.FALSE.equals(result);
    }

    public static <T> void put(String name, String key, T item, int timeoutSeconds) {
        getRedisTemplate().opsForValue().set(getRedisKey(name, key), item, timeoutSeconds, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(String name, String key) {
        return (List<T>) getRedisTemplate().opsForList().range(getRedisKey(name, key), 0, -1);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getListFirstValue(String name, String key) {
        return (T) getRedisTemplate().opsForList().index(getRedisKey(name, key), 0);
    }

    public static void removeListFirstValue(String name, String key) {
        getRedisTemplate().opsForList().leftPop(getRedisKey(name, key));
    }

    public static <T> void pushList(String name, String key, T item) {
        getRedisTemplate().opsForList().rightPush(getRedisKey(name, key), item);
    }

    public static <T> void pushListAll(String name, String key, List<T> items) {
        if (items.size() > 0) getRedisTemplate().opsForList().rightPushAll(getRedisKey(name, key), items.toArray());
    }

    public static <T> void pushList(String name, String key, T item, int timeoutSeconds) {
        multi(new SessionCallback<Object>() {
            @Override public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String redisKey = getRedisKey(name, key);
                @SuppressWarnings("rawtypes") RedisOperations raw = operations;
                raw.opsForList().rightPush(redisKey, item);
                raw.expire(redisKey, timeoutSeconds, TimeUnit.SECONDS);
                return null;
            }
        });
    }

    public static void evict(String name, String key) {
        getRedisTemplate().opsForValue().getOperations().delete(getRedisKey(name, key));
    }

    public static void evict(String name) {
        evictCacheByPattern(name + KEY_LINK_STRS + "*");
    }

    public static void expire(String name, String key, int timeoutSeconds) {
        getRedisTemplate().opsForValue().getOperations().expire(getRedisKey(name, key), timeoutSeconds, TimeUnit.SECONDS);
    }

    public static <T> T multi(SessionCallback<T> callback) {
        return getRedisTemplate().execute(callback);
    }

    public static String getRedisKey(String name, String key) {
        return name + KEY_LINK_STRS + key;
    }

    public static int getListSize(String name, String key) {
        return getRedisTemplate().opsForList().size(getRedisKey(name, key)).intValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(String name, String key, int size) {
        int listSize = getListSize(name, key);
        if (listSize <= 0) return java.util.Collections.emptyList();
        int end = Math.min(size, listSize);
        return (List<T>) getRedisTemplate().opsForList().range(getRedisKey(name, key), 0, end - 1);
    }

    public static void removeList(String name, String key, int size) {
        getRedisTemplate().opsForList().trim(getRedisKey(name, key), size, -1);
    }

    private static void evictCacheByPattern(final String pattern) {
        getRedisTemplate().execute((RedisCallback<Object>) connection -> {
            Set<String> keys = new HashSet<String>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).build());
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
                if (keys.size() > 100) {
                    getRedisTemplate().delete(keys);
                    keys.clear();
                }
            }
            if (!keys.isEmpty()) getRedisTemplate().delete(keys);
            return null;
        });
    }
}
