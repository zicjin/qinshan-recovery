package com.sunwayworld.cloud.module.schedule.schedule.service.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleManager;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CoreScheduleManagerImpl implements CoreScheduleManager {
   @Autowired
   private CoreScheduleTaskConfigService configService;

   @Transactional
   @Override
   public String schedule(String taskId, String uid, String configName, String cron, String param) {
      return this.schedule(taskId, uid, configName, cron, param, null, null);
   }

   @Transactional
   @Override
   public String schedule(String taskId, String uid, String configName, String cron, String param, LocalDateTime startTime, LocalDateTime endTime) {
      Objects.requireNonNull(taskId);
      Objects.requireNonNull(uid);
      CoreScheduleTaskConfigBean filter = BeanUtils.getPropertyListenerProxy(CoreScheduleTaskConfigBean.class);
      filter.setUid(uid);
      CoreScheduleTaskConfigBean config = this.configService.getDao().selectOneIfPresent(filter);
      if (config == null) {
         config = new CoreScheduleTaskConfigBean();
         config.setId(ApplicationContextHelper.getNextSequence("T_CORE_SCHEDULE_TASK_CONFIG"));
         config.setUid(uid);
         config.setTaskId(taskId);
         config.setConfigName(configName);
         config.setCron(cron);
         config.setParam(param);
         config.setActivatedFlag("1");
         config.setActivatedById(LocalContextHelper.getLoginUserId());
         config.setActivatedByName(LocalContextHelper.getLoginUserName());
         config.setActivatedTime(LocalDateTime.now());
         config.setStartTime(startTime);
         config.setEndTime(endTime);
         this.configService.getDao().insert(config);
         return config.getId();
      } else {
         CoreScheduleTaskConfigBean proxy = BeanUtils.getPropertyListenerProxy(config);
         proxy.setConfigName(configName);
         proxy.setCron(cron);
         proxy.setParam(param);
         proxy.setStartTime(startTime);
         proxy.setEndTime(endTime);
         if ("0".equals(config.getActivatedFlag())) {
            proxy.setActivatedFlag("1");
            proxy.setActivatedById(LocalContextHelper.getLoginUserId());
            proxy.setActivatedByName(LocalContextHelper.getLoginUserName());
            proxy.setActivatedTime(LocalDateTime.now());
         }

         this.configService.update(proxy);
         return config.getId();
      }
   }

   @Transactional
   @Override
   public void cancel(String taskId, String uid) {
      Objects.requireNonNull(taskId);
      CoreScheduleTaskConfigBean filter = BeanUtils.getPropertyListenerProxy(CoreScheduleTaskConfigBean.class);
      filter.setUid(uid);
      CoreScheduleTaskConfigBean config = this.configService.getDao().selectOneIfPresent(filter);
      if (config != null && "1".equals(config.getActivatedFlag())) {
         config.setActivatedFlag("0");
         config.setActivatedById(null);
         config.setActivatedByName(null);
         config.setActivatedTime(null);
         this.configService.getDao().update(config, "ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME");
      }
   }
}
