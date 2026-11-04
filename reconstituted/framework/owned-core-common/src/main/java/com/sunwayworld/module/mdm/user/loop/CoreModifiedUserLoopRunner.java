package com.sunwayworld.module.mdm.user.loop;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserService;
import java.time.LocalDateTime;
import java.util.List;

public class CoreModifiedUserLoopRunner implements LoopRunner {
   private static final long RUN_INTERVAL = 600000L;
   private Long lastRunTime = System.currentTimeMillis();

   @Override
   public void run() {
      try {
         GikamConcurrentLocker.isolatedRun(
            CoreModifiedUserLoopRunner.class.getName(),
            () -> {
               CoreModifiedUserService modifiedUserService = ApplicationContextHelper.getBean(CoreModifiedUserService.class);
               CoreModifiedUserBean filter = new CoreModifiedUserBean();
               filter.setActivatedFlag("1");
               filter.setExecuted("0");
               List<CoreModifiedUserBean> modifiedUserList = modifiedUserService.selectListByFilter(
                  SearchFilter.instance()
                     .match("ACTIVATEDFLAG", "1")
                     .filter(MatchPattern.EQ)
                     .match("EXECUTED", "0")
                     .filter(MatchPattern.EQ)
                     .match("SCHEDULEDTIME", LocalDateTime.now())
                     .filter(MatchPattern.TL),
                  new Order[]{Order.asc("ID")}
               );
               if (!modifiedUserList.isEmpty()) {
                  for (CoreModifiedUserBean modifiedUser : modifiedUserList) {
                     try {
                        modifiedUserService.executeScheduled(modifiedUser.getId());
                     } catch (Exception var6) {
                     }
                  }
               }
            }
         );
      } finally {
         this.lastRunTime = System.currentTimeMillis();
      }
   }

   @Override
   public boolean active() {
      return this.lastRunTime + 600000L < System.currentTimeMillis();
   }

   @Override
   public boolean enable() {
      return ApplicationContextHelper.getEnvironment().getProperty("sunway.loop.modified-user", Boolean.class, false);
   }
}
