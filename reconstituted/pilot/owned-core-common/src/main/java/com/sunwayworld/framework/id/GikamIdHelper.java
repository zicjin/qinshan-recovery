package com.sunwayworld.framework.id;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GikamIdHelper {
   public static final int CACHE_TIMEOUT = 600;
   public static final int CACHE_CHECK_PERIOD = 300;
   public static final String IDENTITY = StringUtils.randomUUID();
   private static SnowflakeIdWorker worker;

   public static final long nextId() {
      if (worker == null) {
         initIfNecessary();
      }

      return worker.nextId();
   }

   public static final String nextStringId() {
      return Long.toString(nextId());
   }

   public static final void resetTimeout() {
      GikamConcurrentLocker.blockRun("SUNWAY_WORKERID", () -> {
         if (worker != null) {
            List<String> identityList = RedisHelper.get("SUNWAY", "WORKERID_LIST");
            if (identityList == null) {
               worker = null;
               initIfNecessary();
            } else {
               boolean deleted = identityList.removeIf(i -> RedisHelper.get("SUNWAY_WORKERID", i) == null);
               RedisHelper.expire("SUNWAY_WORKERID", IDENTITY, 600);
               if (deleted) {
                  RedisHelper.put("SUNWAY", "WORKERID_LIST", identityList);
               }
            }
         }
      });
   }

   private static final void initIfNecessary() {
      GikamConcurrentLocker.blockRun(
         "SUNWAY_WORKERID",
         () -> {
            if (worker == null) {
               List<String> identityList = RedisHelper.get("SUNWAY", "WORKERID_LIST");
               if (identityList == null) {
                  List<String> var4 = new ArrayList();
                  var4.add(IDENTITY);
                  RedisHelper.put("SUNWAY", "WORKERID_LIST", var4);
                  RedisHelper.put("SUNWAY_WORKERID", IDENTITY, 0L, 600);
                  worker = new SnowflakeIdWorker(0L);
               } else {
                  identityList.removeIf(ix -> RedisHelper.get("SUNWAY_WORKERID", ix) == null);
                  List<Long> workerIdList = identityList.stream()
                     .map(ix -> RedisHelper.<Long>get("SUNWAY_WORKERID", ix))
                     .filter(w -> w != null)
                     .collect(Collectors.toList());
                  long i = 0L;

                  while (workerIdList.contains(i)) {
                     i++;
                  }

                  RedisHelper.put("SUNWAY_WORKERID", IDENTITY, i, 600);
                  worker = new SnowflakeIdWorker(i);
                  identityList.add(IDENTITY);
                  RedisHelper.put("SUNWAY", "WORKERID_LIST", identityList);
               }
            }
         }
      );
   }
}
