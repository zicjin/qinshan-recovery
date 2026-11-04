package com.sunwayworld.cloud.module.monitor.core.schedule;

import com.sunwayworld.cloud.module.monitor.core.cache.CoreMonitorReqCache;
import com.sunwayworld.cloud.module.monitor.core.queue.CoreMonitorQueue;
import com.sunwayworld.cloud.module.monitor.core.support.CoreMonitorHelper;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAlarmDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorIndicatorDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserDTO;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerService;
import com.sunwayworld.cloud.module.monitor.support.constant.MonitorConstant;
import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.cloud.module.sys.monitor.config.service.CoreSystemMonitorConfigService;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile({"monitor"})
public class CoreMonitorScheduler {
   @Autowired
   private CoreMonitorServerService coreMonitorServerService;
   @Autowired
   private CoreMonitorAppService coreMonitorAppService;
   @Autowired
   private CoreLoginLogService coreLoginLogService;
   @Autowired
   private CoreSystemMonitorConfigService coreSystemMonitorConfigService;
   @Autowired
   private SimpMessagingTemplate simpMessagingTemplate;

   @Scheduled(
      fixedDelay = 5000L
   )
   public void refresh() {
      GikamConcurrentLocker.blockRun(
         "MONITOR_SERVER_NAME::MONITOR_APP_NAME",
         () -> {
            Map<String, CoreMonitorReqBean> monitorReqCacheMap = RedisHelper.get("MONITOR_REQ_NAME", "MONITOR_REQ_KEY");
            if (monitorReqCacheMap == null) {
               monitorReqCacheMap = new ConcurrentHashMap<>();
            }

            CoreMonitorReqBean monitorReqCache = new CoreMonitorReqBean();
            monitorReqCache.setQps(CoreMonitorReqCache.INSTANCE.getQps());
            monitorReqCache.setTps(CoreMonitorReqCache.INSTANCE.getTps());
            monitorReqCacheMap.put(CoreMonitorHelper.getAppId(), monitorReqCache);
            RedisHelper.put("MONITOR_REQ_NAME", "MONITOR_REQ_KEY", monitorReqCacheMap);
            Map<String, CoreMonitorUserDTO> monitorUserCacheMap = RedisHelper.get("MONITOR_USER_NAME", "MONITOR_USER_KEY");
            if (monitorUserCacheMap == null) {
               monitorUserCacheMap = new ConcurrentHashMap<>();
            }

            List<CoreLoginLogBean> onlineUserList = this.coreLoginLogService
               .selectListByFilter(
                  SearchFilter.instance().match("LOGOUTTIME", null).filter(MatchPattern.EQ).match("STATUS", "1").filter(MatchPattern.EQ), new Order[0]
               );
            Integer onlineUserQty = onlineUserList.size();
            CoreMonitorUserDTO monitorUserCache = new CoreMonitorUserDTO();
            monitorUserCache.setOnlineUserQty(onlineUserQty);
            monitorUserCache.getConcurrentUserSet().addAll(CoreMonitorReqCache.INSTANCE.getCus());
            monitorUserCacheMap.put(CoreMonitorHelper.getAppId(), monitorUserCache);
            RedisHelper.put("MONITOR_USER_NAME", "MONITOR_USER_KEY", monitorUserCacheMap);
            Long tps = 0L;
            Long qps = 0L;
            Integer cum = 0;
            Set<String> userSet = new HashSet<>();

            for (CoreMonitorReqBean reqCache : monitorReqCacheMap.values()) {
               tps = tps + reqCache.getTps();
               qps = qps + reqCache.getQps();
            }

            for (CoreMonitorUserDTO userCache : monitorUserCacheMap.values()) {
               userSet.addAll(userCache.getConcurrentUserSet());
            }

            cum = userSet.size();
            tps = tps / 5L + tps % 5L;
            qps = qps / 5L + qps % 5L;
            CoreMonitorReqCache.INSTANCE.clearQps();
            CoreMonitorReqCache.INSTANCE.clearTps();
            CoreMonitorReqCache.INSTANCE.clearCus();
            LocalDateTime now = LocalDateTime.now();
            int refreshTime = MonitorConstant.CORE_MONITOR_REFRESH_TIME;
            int requiredSize = MonitorConstant.CORE_MONITOR_TIME_RANGE * 60 * 1000 / refreshTime;
            Queue<CoreMonitorMessageBean> monitorMessageCacheQueue = RedisHelper.get("MONITOR_MESSAGE_QUEUE_NAME", "MONITOR_MESSAGE_QUEUE_KEY");
            if (monitorMessageCacheQueue == null) {
               monitorMessageCacheQueue = new ConcurrentLinkedQueue<>();
            }

            CoreMonitorReqBean monitorReq = new CoreMonitorReqBean();
            monitorReq.setQps(qps);
            monitorReq.setTps(tps);
            monitorReq.setId(ApplicationContextHelper.getNextIdentity());
            monitorReq.setCreatedTime(now);
            monitorReq.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(monitorReq.getCreatedTime()));
            CoreMonitorQueue.INSTANCE.offerMonitorReq(monitorReq);
            Long serverRumtimeId = ApplicationContextHelper.getNextIdentity();
            CoreMonitorUserBean monitorUser = new CoreMonitorUserBean();
            monitorUser.setId(serverRumtimeId);
            monitorUser.setOnlineUserQty(NumberUtils.parseLong(onlineUserQty));
            monitorUser.setConcurrentUserQty(NumberUtils.parseLong(cum));
            monitorUser.setCreatedTime(now);
            monitorUser.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(monitorUser.getCreatedTime()));
            CoreMonitorQueue.INSTANCE.offerMonitorUser(monitorUser);
            List<CoreSystemMonitorConfigBean> configBeanList = this.coreSystemMonitorConfigService.selectAll();
            configBeanList = configBeanList.stream().filter(e -> "warn".equals(e.getConfigCategory())).collect(Collectors.toList());
            Map<String, String> configMap = configBeanList.stream().collect(HashMap::new, (map, item) -> {
               String var10000 = map.put(item.getId(), item.getConfigValue());
            }, HashMap::putAll);
            CoreMonitorServerRuntimeBean serverRuntime = CoreMonitorHelper.getServerRuntimeInfo();
            Long serverRuntimeId = ApplicationContextHelper.getNextIdentity();
            serverRuntime.setId(serverRuntimeId);
            serverRuntime.setCreatedTime(now);
            serverRuntime.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(serverRuntime.getCreatedTime()));
            String configCpu = configMap.get("cpuThreshold");
            if (serverRuntime.getServerCpuUtilization() > Integer.parseInt(configCpu)) {
               serverRuntime.setServerCpuStatus("alarm");
            }

            String configMemory = configMap.get("memoryThreshold");
            if (serverRuntime.getServerRamUtilization() > Integer.parseInt(configMemory)) {
               serverRuntime.setServerRamStatus("alarm");
            }

            CoreMonitorQueue.INSTANCE.offerServerRuntime(serverRuntime);
            if ("alarm".equals(serverRuntime.getServerCpuStatus()) || "alarm".equals(serverRuntime.getServerRamStatus())) {
               CoreMonitorMessageBean monitorMessage = this.buildMonitorMessageByServerRuntime("alarm", serverRuntime);
               CoreMonitorQueue.INSTANCE.offerMonitorMessage(monitorMessage);
               monitorMessageCacheQueue.offer(monitorMessage);
            }

            CoreMonitorAppRuntimeBean appRuntime = CoreMonitorHelper.getAppRuntimeInfo();
            appRuntime.setId(ApplicationContextHelper.getNextIdentity());
            appRuntime.setServerRunTimeId(serverRumtimeId);
            appRuntime.setCreatedTime(now);
            appRuntime.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(appRuntime.getCreatedTime()));
            if (appRuntime.getAppCpuUtilization() > Integer.parseInt(configCpu)) {
               appRuntime.setAppCpuStatus("alarm");
            }

            if (appRuntime.getAppRamUtilization() > Integer.parseInt(configMemory)) {
               appRuntime.setAppRamStatus("alarm");
            }

            CoreMonitorQueue.INSTANCE.offerAppRuntime(appRuntime);
            if ("alarm".equals(appRuntime.getAppCpuStatus()) || "alarm".equals(appRuntime.getAppRamStatus())) {
               CoreMonitorMessageBean monitorMessage = this.buildMonitorMessageByAppRuntime("alarm", appRuntime);
               CoreMonitorQueue.INSTANCE.offerMonitorMessage(monitorMessage);
               monitorMessageCacheQueue.offer(monitorMessage);
            }

            RedisHelper.put("MONITOR_MESSAGE_QUEUE_NAME", "MONITOR_MESSAGE_QUEUE_KEY", monitorMessageCacheQueue);
            CoreMonitorMessageDTO coreMonitorMessage = new CoreMonitorMessageDTO();
            Map<String, Long> coreSystemMonitorConfig = new HashMap<>();
            coreSystemMonitorConfig.put("serverCpuAbnormal", Long.valueOf(configCpu));
            coreSystemMonitorConfig.put("serverRamAbnormal", Long.valueOf(configMemory));
            coreMonitorMessage.setMonitorConfig(coreSystemMonitorConfig);
            List<CoreMonitorServerDTO> serverMessageList = this.buildServerMessage(now, requiredSize, serverRuntime, appRuntime);
            CoreMonitorIndicatorDTO systemIndicator = this.buildSystemIndicator(now, requiredSize, monitorUser, monitorReq);
            List<CoreMonitorMessageBean> monitorMessageList = new ArrayList<>();
            monitorMessageCacheQueue.forEach(e -> monitorMessageList.add(e));
            CoreMonitorAlarmDTO systemAlarm = this.buildSystemAlarm(monitorMessageList);
            coreMonitorMessage.setServerList(serverMessageList);
            coreMonitorMessage.setSystemAlarm(systemAlarm);
            coreMonitorMessage.setSystemMessageList(monitorMessageList);
            coreMonitorMessage.setSystemIndicator(systemIndicator);
            this.simpMessagingTemplate.convertAndSend("/topic/system", coreMonitorMessage);
         }
      );
   }

   private CoreMonitorIndicatorDTO buildSystemIndicator(
      LocalDateTime time, int requiredIndicatorSize, CoreMonitorUserBean monitorUser, CoreMonitorReqBean monitorReq
   ) {
      CoreMonitorIndicatorDTO systemIndicator = new CoreMonitorIndicatorDTO();
      Queue<CoreMonitorUserBean> monitorUserCacheQueue = RedisHelper.get("MONITOR_SYSTEMUSER_QUEUE_NAME", "MONITOR_SYSTEMUSER_QUEUE_KEY");
      if (monitorUserCacheQueue == null) {
         monitorUserCacheQueue = new ConcurrentLinkedQueue<>();
      }

      if (monitorUserCacheQueue.size() == requiredIndicatorSize) {
         monitorUserCacheQueue.poll();
      }

      monitorUserCacheQueue.offer(monitorUser);
      RedisHelper.put("MONITOR_SYSTEMUSER_QUEUE_NAME", "MONITOR_SYSTEMUSER_QUEUE_KEY", monitorUserCacheQueue);
      List<CoreMonitorUserBean> monitorUserList = new ArrayList<>();
      int monitorUserCacheQueueSize = monitorUserCacheQueue.size();
      LocalDateTime monitorUserEndTime = LocalDateTime.from(time);
      CoreMonitorUserBean monitorUserHeader = monitorUserCacheQueue.peek();
      if (monitorUserHeader != null) {
         monitorUserEndTime = monitorUserHeader.getCreatedTime();
      }

      for (int i = 0; i < requiredIndicatorSize - monitorUserCacheQueueSize; i++) {
         CoreMonitorUserBean emptyMonitorUser = new CoreMonitorUserBean();
         monitorUserEndTime = monitorUserEndTime.minusSeconds(MonitorConstant.CORE_MONITOR_REFRESH_TIME.intValue() / 1000L);
         emptyMonitorUser.setOnlineUserQty(0L);
         emptyMonitorUser.setConcurrentUserQty(0L);
         emptyMonitorUser.setCreatedTime(monitorUserEndTime);
         emptyMonitorUser.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(monitorUserEndTime));
         monitorUserList.add(emptyMonitorUser);
      }

      monitorUserCacheQueue.forEach(f -> monitorUserList.add(0, f));
      Queue<CoreMonitorReqBean> monitorReqCacheQueue = RedisHelper.get("MONITOR_REQ_QUEUE_CACHE_NAME", "MONITOR_REQ_QUEUE_CHACHE_KEY");
      if (monitorReqCacheQueue == null) {
         monitorReqCacheQueue = new ConcurrentLinkedQueue<>();
      }

      if (monitorReqCacheQueue.size() == requiredIndicatorSize) {
         monitorReqCacheQueue.poll();
      }

      monitorReqCacheQueue.offer(monitorReq);
      RedisHelper.put("MONITOR_REQ_QUEUE_CACHE_NAME", "MONITOR_REQ_QUEUE_CHACHE_KEY", monitorReqCacheQueue);
      List<CoreMonitorReqBean> monitorReqList = new ArrayList<>();
      int monitorReqCacheQueueSize = monitorReqCacheQueue.size();
      LocalDateTime monitorReqEndTime = LocalDateTime.from(time);
      CoreMonitorReqBean monitorReqHeader = monitorReqCacheQueue.peek();
      if (monitorReqHeader != null) {
         monitorReqEndTime = monitorReqHeader.getCreatedTime();
      }

      for (int i = 0; i < requiredIndicatorSize - monitorReqCacheQueueSize; i++) {
         CoreMonitorReqBean emptyMonitorReq = new CoreMonitorReqBean();
         monitorReqEndTime = monitorReqEndTime.minusSeconds(MonitorConstant.CORE_MONITOR_REFRESH_TIME / 1000);
         emptyMonitorReq.setTps(0L);
         emptyMonitorReq.setQps(0L);
         emptyMonitorReq.setCreatedTime(monitorReqEndTime);
         emptyMonitorReq.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(monitorReqEndTime));
         monitorReqList.add(emptyMonitorReq);
      }

      monitorReqCacheQueue.forEach(f -> monitorReqList.add(0, f));
      systemIndicator.setCurSystemUser(monitorUser);
      systemIndicator.setCurSystemReq(monitorReq);
      systemIndicator.setSystemUserList(monitorUserList);
      systemIndicator.setSystemReqList(monitorReqList);
      return systemIndicator;
   }

   private List<CoreMonitorServerDTO> buildServerMessage(
      LocalDateTime time, int requiredSize, CoreMonitorServerRuntimeBean serverRuntime, CoreMonitorAppRuntimeBean appRuntime
   ) {
      Map<String, List<CoreMonitorServerRuntimeBean>> serverRuntimeCacheMap = RedisHelper.get("MONITOR_SERVER_INFO_QUEUE_NAME", "MONITOR_SERVER_INFO_QUEUE_KEY");
      if (serverRuntimeCacheMap == null) {
         serverRuntimeCacheMap = new ConcurrentHashMap<>();
      }

      List<CoreMonitorServerRuntimeBean> serverRuntimeCacheList = serverRuntimeCacheMap.get(serverRuntime.getServerId());
      if (serverRuntimeCacheList == null) {
         serverRuntimeCacheList = new LinkedList<>();
      }

      if (serverRuntimeCacheList.size() == requiredSize) {
         serverRuntimeCacheList.remove(0);
      }

      serverRuntimeCacheList.add(serverRuntime);
      serverRuntimeCacheMap.put(serverRuntime.getServerId(), serverRuntimeCacheList);
      RedisHelper.put("MONITOR_SERVER_INFO_QUEUE_NAME", "MONITOR_SERVER_INFO_QUEUE_KEY", serverRuntimeCacheMap);
      Map<String, CoreMonitorAppRuntimeBean> appRuntimeCacheMap = RedisHelper.get("MONITOR_APP_INFO_NAME", "MONITOR_APP_INFO_KEY");
      if (appRuntimeCacheMap == null) {
         appRuntimeCacheMap = new ConcurrentHashMap<>();
      }

      appRuntimeCacheMap.put(appRuntime.getAppId(), appRuntime);
      RedisHelper.put("MONITOR_APP_INFO_NAME", "MONITOR_APP_INFO_KEY", appRuntimeCacheMap);
      List<CoreMonitorServerBean> allServerList = this.coreMonitorServerService.selectAll();
      List<CoreMonitorAppBean> allAppList = this.coreMonitorAppService.selectAll();
      List<CoreMonitorServerDTO> serverDTOList = new ArrayList<>();
      List<CoreMonitorServerRuntimeBean> curServerRuntimeList = new ArrayList<>();
      serverRuntimeCacheMap.forEach((k, v) -> curServerRuntimeList.add(v.get(v.size() - 1)));

      for (CoreMonitorServerBean server : allServerList) {
         CoreMonitorServerDTO serverDTO = new CoreMonitorServerDTO();
         serverDTO.setServerInfo(server);
         CoreMonitorServerRuntimeBean curServerRuntime = curServerRuntimeList.stream()
            .filter(f -> f.getServerId().equals(server.getId()))
            .findFirst()
            .orElse(null);
         if (curServerRuntime == null) {
            curServerRuntime = new CoreMonitorServerRuntimeBean();
            curServerRuntime.setServerId(server.getId());
         }

         serverDTO.setCurServerRuntime(curServerRuntime);
         List<CoreMonitorAppRuntimeBean> appRuntimeList = new ArrayList<>();

         for (CoreMonitorAppBean monitorApp : allAppList.stream().filter(f -> f.getServerId().equals(server.getId())).collect(Collectors.toList())) {
            CoreMonitorAppRuntimeBean curAppRuntimeCache = appRuntimeCacheMap.values()
               .stream()
               .filter(f -> f.getServerId().equals(server.getId()) && f.getAppId().equals(monitorApp.getId()))
               .findFirst()
               .orElse(null);
            if (curAppRuntimeCache == null) {
               curAppRuntimeCache = new CoreMonitorAppRuntimeBean();
               curAppRuntimeCache.setAppId(monitorApp.getId());
               curAppRuntimeCache.setAppRamUtilization(0L);
               curAppRuntimeCache.setAppCpuUtilization(0L);
               curAppRuntimeCache.setStatus("stop");
            }

            appRuntimeList.add(curAppRuntimeCache);
         }

         serverDTO.setAppRuntimeList(appRuntimeList);
         List<CoreMonitorServerRuntimeBean> serverRuntimeList = new ArrayList<>();
         List<CoreMonitorServerRuntimeBean> monitorServerRuntimeList = serverRuntimeCacheMap.get(server.getId());
         if (monitorServerRuntimeList == null) {
            monitorServerRuntimeList = new LinkedList<>();
         }

         int serverRuntimeSize = monitorServerRuntimeList.size();
         LocalDateTime endTime = time;
         if (!monitorServerRuntimeList.isEmpty()) {
            CoreMonitorServerRuntimeBean headMonitorServerRuntime = monitorServerRuntimeList.get(0);
            endTime = headMonitorServerRuntime.getCreatedTime();
         }

         for (int i = 0; i < requiredSize - serverRuntimeSize; i++) {
            CoreMonitorServerRuntimeBean emptyServerRuntime = new CoreMonitorServerRuntimeBean();
            endTime = endTime.minusSeconds(MonitorConstant.CORE_MONITOR_REFRESH_TIME / 1000);
            emptyServerRuntime.setServerCpuUtilization(0L);
            emptyServerRuntime.setServerRamUtilization(0L);
            emptyServerRuntime.setServerDiskUtilization(0L);
            emptyServerRuntime.setCreatedTime(endTime);
            emptyServerRuntime.setExt$Item("formattime", DateTimeUtils.formatLocalDateTime(endTime));
            serverRuntimeList.add(emptyServerRuntime);
         }

         monitorServerRuntimeList.forEach(f -> serverRuntimeList.add(0, f));
         serverDTO.setServerRuntimeList(serverRuntimeList);
         serverDTOList.add(serverDTO);
      }

      return serverDTOList;
   }

   private CoreMonitorMessageBean buildMonitorMessageByAppRuntime(String appStatus, CoreMonitorAppRuntimeBean coreAppRuntime) {
      CoreMonitorMessageBean coreSystemMessage = new CoreMonitorMessageBean();
      coreSystemMessage.setId(ApplicationContextHelper.getNextIdentity());
      coreSystemMessage.setCategory(appStatus);
      coreSystemMessage.setCpuUtilization(coreAppRuntime.getAppCpuUtilization());
      coreSystemMessage.setRamUtilization(coreAppRuntime.getAppRamUtilization());
      coreSystemMessage.setDiskUtilization(0L);
      coreSystemMessage.setServerId(coreAppRuntime.getServerId());
      coreSystemMessage.setAppId(coreAppRuntime.getAppId());
      coreSystemMessage.setCreatedTime(LocalDateTime.now());
      coreSystemMessage.setType("app");
      if (appStatus.equals("abnormal")) {
         if ("abnormal".equals(coreAppRuntime.getAppCpuStatus())) {
            coreSystemMessage.setTitle(
               CoreMonitorHelper.buildMessageInfo(
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.APP_TITLE"),
                  coreSystemMessage.getAppId(),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.CPU_LOAD"),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ABNORMAL")
               )
            );
            coreSystemMessage.setInfo(CoreMonitorHelper.getStackInfo());
         } else {
            coreSystemMessage.setTitle(
               CoreMonitorHelper.buildMessageInfo(
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.APP_TITLE"),
                  coreSystemMessage.getAppId(),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.RAM_LOAD"),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ABNORMAL")
               )
            );
            coreSystemMessage.setInfo(coreSystemMessage.getTitle());
         }
      } else if ("alarm".equals(coreAppRuntime.getAppCpuStatus())) {
         coreSystemMessage.setTitle(
            CoreMonitorHelper.buildMessageInfo(
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.APP_TITLE"),
               coreSystemMessage.getAppId(),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.CPU_LOAD"),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ALARM")
            )
         );
         coreSystemMessage.setInfo(CoreMonitorHelper.getStackInfo());
      } else if ("alarm".equals(coreAppRuntime.getAppRamStatus())) {
         coreSystemMessage.setTitle(
            CoreMonitorHelper.buildMessageInfo(
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.APP_TITLE"),
               coreSystemMessage.getAppId(),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.RAM_LOAD"),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ALARM")
            )
         );
         coreSystemMessage.setInfo(coreSystemMessage.getTitle());
      }

      return coreSystemMessage;
   }

   private CoreMonitorMessageBean buildMonitorMessageByServerRuntime(String serverStatus, CoreMonitorServerRuntimeBean coreServerRuntime) {
      CoreMonitorMessageBean coreSystemMessage = new CoreMonitorMessageBean();
      coreSystemMessage.setId(ApplicationContextHelper.getNextIdentity());
      coreSystemMessage.setCategory(serverStatus);
      coreSystemMessage.setCpuUtilization(coreServerRuntime.getServerCpuUtilization());
      coreSystemMessage.setRamUtilization(coreServerRuntime.getServerRamUtilization());
      coreSystemMessage.setDiskUtilization(coreServerRuntime.getServerDiskUtilization());
      coreSystemMessage.setServerId(coreServerRuntime.getServerId());
      coreSystemMessage.setCreatedTime(LocalDateTime.now());
      coreSystemMessage.setType("server");
      if (serverStatus.equals("abnormal")) {
         if ("abnormal".equals(coreServerRuntime.getServerCpuStatus())) {
            coreSystemMessage.setTitle(
               CoreMonitorHelper.buildMessageInfo(
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.SERVER_TITLE"),
                  coreSystemMessage.getServerId(),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.CPU_LOAD"),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ABNORMAL")
               )
            );
            coreSystemMessage.setInfo(coreSystemMessage.getTitle());
         } else {
            coreSystemMessage.setTitle(
               CoreMonitorHelper.buildMessageInfo(
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.SERVER_TITLE"),
                  coreSystemMessage.getServerId(),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.RAM_LOAD"),
                  I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ABNORMAL")
               )
            );
            coreSystemMessage.setInfo(coreSystemMessage.getTitle());
         }
      } else if ("alarm".equals(coreServerRuntime.getServerCpuStatus())) {
         coreSystemMessage.setTitle(
            CoreMonitorHelper.buildMessageInfo(
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.SERVER_TITLE"),
               coreSystemMessage.getServerId(),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.CPU_LOAD"),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ALARM")
            )
         );
         coreSystemMessage.setInfo(coreSystemMessage.getTitle());
      } else if ("alarm".equals(coreServerRuntime.getServerRamStatus())) {
         coreSystemMessage.setTitle(
            CoreMonitorHelper.buildMessageInfo(
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.SERVER_TITLE"),
               coreSystemMessage.getServerId(),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.RAM_LOAD"),
               I18nHelper.getMessage("GIKAM.CORE.MONITOR.TIP.ALARM")
            )
         );
         coreSystemMessage.setInfo(coreSystemMessage.getTitle());
      }

      return coreSystemMessage;
   }

   private CoreMonitorAlarmDTO buildSystemAlarm(List<CoreMonitorMessageBean> monitorMessageList) {
      CoreMonitorAlarmDTO systemAlarm = new CoreMonitorAlarmDTO();
      Long normalQty = monitorMessageList.stream().filter(f -> "normal".equals(f.getCategory())).count();
      Long alarmQty = monitorMessageList.stream().filter(f -> "alarm".equals(f.getCategory())).count();
      Long abnormalQty = monitorMessageList.stream().filter(f -> "abnormal".equals(f.getCategory())).count();
      systemAlarm.setNormalQty(normalQty);
      systemAlarm.setAlarmQty(alarmQty);
      systemAlarm.setAbnormalQty(abnormalQty);
      return systemAlarm;
   }
}
