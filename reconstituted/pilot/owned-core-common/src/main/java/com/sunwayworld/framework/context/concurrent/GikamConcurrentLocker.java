/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.concurrent;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.exception.ConcurrentException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.TransactionUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class GikamConcurrentLocker {
    private static final String UNIQUE_KEY = UUID.randomUUID().toString().replace("-", "");
    public static final String LOCKER_ISOLATED_CACHE_NAME = "GIKAM_ISOLATED_CONCURRENT";
    public static final String LOCKER_BLOCK_CACHE_NAME = "GIKAM_BLOCK_CONCURRENT";
    public static final int DEFAULT_EXPIRE_SECONDS = 300;

    public static void isolated(String key) {
        TransactionUtils.transactionRequired();
        String cacheValue = UNIQUE_KEY + "$I" + Thread.currentThread().getId();
        String cacheKey = RedisHelper.getRedisKey(LOCKER_ISOLATED_CACHE_NAME, key);
        SessionCallback<Boolean> callback = GikamConcurrentLocker.getLockCallback(cacheKey, cacheValue, 300);
        if (!RedisHelper.multi(callback).booleanValue()) {
            throw new ConcurrentException(I18nHelper.getMessage("GIKAM.CONCURRENT.ISOLATED", new String[0]));
        }
        TransactionUtils.runAfterCompletion(() -> GikamConcurrentLocker.releaseLock(LOCKER_ISOLATED_CACHE_NAME, key));
    }

    public static void isolatedRun(String key, Runnable runnable) {
        GikamConcurrentLocker.isolatedRun(key, runnable, 300);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void isolatedRun(String key, Runnable runnable, int timeoutSeconds) {
        String cacheValue = UNIQUE_KEY + "$I" + Thread.currentThread().getId();
        String cacheKey = RedisHelper.getRedisKey(LOCKER_ISOLATED_CACHE_NAME, key);
        SessionCallback<Boolean> callback = GikamConcurrentLocker.getLockCallback(cacheKey, cacheValue, timeoutSeconds);
        if (RedisHelper.multi(callback).booleanValue()) {
            boolean transactionActive = TransactionSynchronizationManager.isSynchronizationActive();
            if (transactionActive) {
                TransactionUtils.runAfterCompletion(() -> GikamConcurrentLocker.releaseLock(LOCKER_ISOLATED_CACHE_NAME, key));
            }
            try {
                runnable.run();
            }
            finally {
                if (!transactionActive) {
                    GikamConcurrentLocker.releaseLock(LOCKER_ISOLATED_CACHE_NAME, key);
                }
            }
        } else {
            throw new ConcurrentException(I18nHelper.getMessage("GIKAM.CONCURRENT.ISOLATED", new String[0]));
        }
    }

    public static void updateIsolatedRunLockTimeout(String key, final int timeoutSeconds) {
        final String cacheValue = UNIQUE_KEY + "$I" + Thread.currentThread().getId();
        final String cacheKey = RedisHelper.getRedisKey(LOCKER_ISOLATED_CACHE_NAME, key);
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>(){

            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                String value = (String)operations.opsForValue().get(cacheKey);
                if (value != null && cacheValue.equals(value)) {
                    operations.opsForValue().set(cacheKey, cacheValue, Math.max((long)timeoutSeconds, 300L), TimeUnit.SECONDS);
                    return true;
                }
                return false;
            }
        };
        if (!RedisHelper.multi(callback).booleanValue()) {
            throw new ConcurrentException(I18nHelper.getMessage("GIKAM.CONCURRENT.ISOLATED.ONLY_UPDATE_OWN_LOCK_TIMEOUT", new String[0]));
        }
    }

    public static void block(String key) {
        TransactionUtils.transactionRequired();
        String cacheValue = UNIQUE_KEY + "$B" + Thread.currentThread().getId();
        String cacheKey = RedisHelper.getRedisKey(LOCKER_BLOCK_CACHE_NAME, key);
        SessionCallback<Boolean> callback = GikamConcurrentLocker.getLockCallback(cacheKey, cacheValue, 300);
        while (!RedisHelper.multi(callback).booleanValue()) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException interruptedException) {}
        }
        TransactionUtils.runAfterCompletion(() -> GikamConcurrentLocker.releaseLock(LOCKER_BLOCK_CACHE_NAME, key));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void blockRun(String key, int timeoutSeconds, Runnable runnable) {
        String cacheValue = UNIQUE_KEY + "$B" + Thread.currentThread().getId();
        String cacheKey = RedisHelper.getRedisKey(LOCKER_BLOCK_CACHE_NAME, key);
        SessionCallback<Boolean> callback = GikamConcurrentLocker.getLockCallback(cacheKey, cacheValue, timeoutSeconds);
        while (!RedisHelper.multi(callback).booleanValue()) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException interruptedException) {}
        }
        boolean transactionActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (transactionActive) {
            TransactionUtils.runAfterCompletion(() -> GikamConcurrentLocker.releaseLock(LOCKER_BLOCK_CACHE_NAME, key));
        }
        try {
            runnable.run();
        }
        finally {
            if (!transactionActive) {
                GikamConcurrentLocker.releaseLock(LOCKER_BLOCK_CACHE_NAME, key);
            }
        }
    }

    public static void blockRun(String key, Runnable runnable) {
        GikamConcurrentLocker.blockRun(key, 0, runnable);
    }

    public static void updateBlockRunLockTimeout(String key, final int timeoutSeconds) {
        final String cacheValue = UNIQUE_KEY + "$B" + Thread.currentThread().getId();
        final String cacheKey = RedisHelper.getRedisKey(LOCKER_BLOCK_CACHE_NAME, key);
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>(){

            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                String value = (String)operations.opsForValue().get(cacheKey);
                if (value != null && cacheValue.equals(value)) {
                    operations.opsForValue().set(cacheKey, cacheValue, Math.max((long)timeoutSeconds, 300L), TimeUnit.SECONDS);
                    return true;
                }
                return false;
            }
        };
        if (!RedisHelper.multi(callback).booleanValue()) {
            throw new ConcurrentException(I18nHelper.getMessage("GIKAM.CONCURRENT.BLOCK.ONLY_UPDATE_OWN_LOCK_TIMEOUT", new String[0]));
        }
    }

    public static void releaseLock(String name, String key) {
        RedisHelper.evict(name, key);
    }

    private static SessionCallback<Boolean> getLockCallback(final String cacheKey, final String expectedValue, final int timeout) {
        return new SessionCallback<Boolean>(){

            @Override
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                String value = (String)operations.opsForValue().get(cacheKey);
                if (value != null) {
                    return expectedValue.equals(value);
                }
                return !Boolean.FALSE.equals(operations.opsForValue().setIfAbsent(cacheKey, expectedValue, Math.max((long)timeout, 300L), TimeUnit.SECONDS));
            }
        };
    }
}
