package com.sunwayworld.cloud.module.equipt.monitor;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.cloud.module.equipt.equipt.service.CoreEquiptService;
import com.sunwayworld.cloud.module.equipt.usagerecord.service.impl.EquiptMonitorSocketConnect;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile({"equipt-monitor"})
public class CoreEquiptStatusMonitorSchedule {
   @Transactional
   @Scheduled(
      fixedDelay = 10000L
   )
   public void monitorSchedule() {
      GikamConcurrentLocker.isolatedRun("MONITOR_CONFIG_SCHEDULE", () -> this.monitor());
   }

   private void monitor() {
      CoreEquiptService equiptService = ApplicationContextHelper.getBean(CoreEquiptService.class);

      for (CoreEquiptBean equipt : equiptService.selectAll()) {
         if (!StringUtils.isEmpty(equipt.getIp()) && !StringUtils.isEmpty(equipt.getMonitorCode()) && equipt.getThreshold() != null) {
            new EquiptMonitorSocketConnect().start(equipt.getIp(), equipt.getPort().intValue(), equipt.getMonitorCode(), equipt.getThreshold());
         }
      }
   }
}
