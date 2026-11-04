package com.sunwayworld.cloud.module.schedule.support;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.Map;
import java.util.Optional;

public enum CoreScheduleJobContainer {
   INSTANCE;

   private static final String REDIS_NAME = "SCHEDULE_JOB";
   private static final String REDIS_KEY = "ALL";

   public int getScheduleJobSize() {
      return this.getJobMap().size();
   }

   public void setJobMap(Map<String, CoreScheduleJob> jobMap, boolean updateOnly) {
      GikamConcurrentLocker.blockRun("CORE_SCHEDULE_TASK_UPDATE_JOBS", () -> {
         Map<String, CoreScheduleJob> jobMapCache = this.getJobMap();
         if (updateOnly) {
            jobMap.forEach((k, v) -> {
               if (jobMapCache.containsKey(k)) {
                  jobMapCache.put(k, v);
               }
            });
         } else {
            jobMapCache.entrySet().removeIf(e -> !jobMap.containsKey(e.getKey()));
            jobMap.entrySet().stream().filter(e -> !jobMapCache.containsKey(e.getKey())).forEach(e -> {
               CoreScheduleJob var10000 = jobMapCache.put(e.getKey(), e.getValue());
            });
            jobMap.forEach((k, v) -> {
               if (jobMapCache.containsKey(k)) {
                  CoreScheduleJob cacheJob = jobMapCache.get(k);
                  if (cacheJob.getTaskConfig().updated(v.getTaskConfig())) {
                     jobMapCache.put(k, v);
                  }
               }
            });
         }

         RedisHelper.put("SCHEDULE_JOB", "ALL", jobMapCache);
      });
   }

   public void clear() {
      RedisHelper.evict("SCHEDULE_JOB", "ALL");
   }

   public Map<String, CoreScheduleJob> getJobMap() {
      return Optional.ofNullable(RedisHelper.<Map<String, CoreScheduleJob>>get("SCHEDULE_JOB", "ALL")).orElse(CollectionUtils.emptyMap());
   }
}
