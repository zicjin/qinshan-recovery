package com.sunwayworld.framework.context.concurrent;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

public abstract class GikamGuardianLocker {
   private static final Logger logger = LogManager.getLogger(GikamGuardianLocker.class);
   public static final int DEFAULT_EXPIRE_SECONDS = 12;
   public static final int LOOP_INTERVAL = 4;
   public static final String LOCKER_ISOLATED_CACHE_NAME = "GUARDIAN_ISOLATED";
   private static final String UNIQUE_KEY = UUID.randomUUID().toString().replace("-", "");
   private static final Set<String> keyList = new CopyOnWriteArraySet<>();

   public static void isolatedRun(String key, Runnable runnable) {
      String cacheValue = UNIQUE_KEY + "$I" + Thread.currentThread().getId();
      String cacheKey = RedisHelper.getRedisKey("GUARDIAN_ISOLATED", key);
      SessionCallback<Boolean> callback = getLockCallback(cacheKey, cacheValue, 12);
      if (RedisHelper.multi(callback)) {
         try {
            keyList.add(key);
            runnable.run();
         } finally {
            keyList.remove(key);
            RedisHelper.evict("GUARDIAN_ISOLATED", key);
         }
      } else {
         logger.debug("Unable run (" + key + ") concurrently");
      }
   }

   private static SessionCallback<Boolean> getLockCallback(String cacheKey, String expectedValue, int timeout) {
      return new SessionCallback<Boolean>() {
         public Boolean execute(RedisOperations operations) throws DataAccessException {
            String value = (String)operations.opsForValue().get(cacheKey);
            return value != null
               ? expectedValue.equals(value)
               : !Boolean.FALSE.equals(operations.opsForValue().setIfAbsent(cacheKey, expectedValue, Math.max((long)timeout, 12L), TimeUnit.SECONDS));
         }
      };
   }

   static {
      Thread t = new Thread() {
         @Override
         public void run() {
            while (true) {
               try {
                  Thread.sleep(4000L);

                  for (String key : new ArrayList<>(GikamGuardianLocker.keyList)) {
                     RedisHelper.expire("GUARDIAN_ISOLATED", key, 12);
                  }
               } catch (Exception var4) {
               }
            }
         }
      };
      t.setDaemon(true);
      t.setPriority(10);
      t.start();
   }
}
