package com.sunwayworld.cloud.module.sys.monitor.config.schedule;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailFileBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.cloud.module.email.service.CoreEmailReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailService;
import com.sunwayworld.cloud.module.monitor.core.support.CoreMonitorHelper;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.cloud.module.sys.monitor.config.service.CoreSystemMonitorConfigService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import com.sunwayworld.module.sys.sms.core.CoreSmsManager;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile({"monitor"})
public class CoreSystemMonitorConfigScheduler {
   @Autowired
   private CoreLoginLogService coreLoginLogService;
   @Autowired
   private CoreSystemMonitorConfigService coreSystemMonitorConfigService;
   @Autowired
   private CoreEmailService coreEmailService;
   @Autowired
   private CoreEmailReceipentService coreEmailReceipentService;
   @Autowired(
      required = false
   )
   private CoreSmsManager smsManager;
   private HikariPoolMXBean proxy;
   @Resource
   private HikariDataSource hikariDataSource;

   @Scheduled(
      fixedDelay = 10000L
   )
   public void refresh() {
      GikamConcurrentLocker.blockRun(
         "MONITOR_SERVER_NAME::MONITOR_APP_NAME",
         () -> {
            List<CoreSystemMonitorConfigBean> configBeanList = this.coreSystemMonitorConfigService.selectAll();
            Map<String, String> configMap = configBeanList.stream().collect(HashMap::new, (map, item) -> {
               String var10000 = map.put(item.getId(), item.getConfigValue());
            }, HashMap::putAll);
            CoreMonitorServerRuntimeBean serverRuntime = CoreMonitorHelper.getServerRuntimeInfo();
            String port = ApplicationContextHelper.getEnvironment().getProperty("server.port");
            StringBuilder systemInfoPortBuilder = new StringBuilder();
            systemInfoPortBuilder.append(serverRuntime.getServerId()).append(":").append(port);
            String configCpu = configMap.get("cpuThreshold");
            if (serverRuntime.getServerCpuUtilization() > Long.valueOf(configCpu)) {
               String alarmContent = I18nHelper.getMessage(
                  "T_CORE_SYSTEM_MONITOR_CONFIG.ALARM.SMS_CONTENT",
                  "CPU",
                  systemInfoPortBuilder.toString(),
                  configCpu,
                  serverRuntime.getServerCpuUtilization().toString()
               );
               String emailSubject = "CPU告警通知";
               this.sendMessage(alarmContent, emailSubject);
            }

            String configMemory = configMap.get("memoryThreshold");
            if (serverRuntime.getServerRamUtilization() > Long.valueOf(configMemory)) {
               String alarmContent = I18nHelper.getMessage(
                  "T_CORE_SYSTEM_MONITOR_CONFIG.ALARM.SMS_CONTENT",
                  "内存",
                  systemInfoPortBuilder.toString(),
                  configMemory,
                  serverRuntime.getServerRamUtilization().toString()
               );
               String emailSubject = "内存告警通知";
               this.sendMessage(alarmContent, emailSubject);
            }

            String configHardDisk = configMap.get("hardDiskThreshold");
            if (serverRuntime.getServerDiskUtilization() > Long.valueOf(configHardDisk)) {
               String alarmContent = I18nHelper.getMessage(
                  "T_CORE_SYSTEM_MONITOR_CONFIG.ALARM.SMS_CONTENT",
                  "硬盘",
                  systemInfoPortBuilder.toString(),
                  configHardDisk,
                  serverRuntime.getServerDiskUtilization().toString()
               );
               String emailSubject = "硬盘告警通知";
               this.sendMessage(alarmContent, emailSubject);
            }

            String configDbConnectionPool = configMap.get("dbConnectionPoolThreshold");

            try {
               if (null == this.proxy) {
                  MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                  ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (" + this.hikariDataSource.getPoolName() + ")");
                  this.proxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);
               } else {
                  double dbConnectionPoolUtilization = (1.0 - (double)this.proxy.getIdleConnections() / this.hikariDataSource.getMaximumPoolSize()) * 100.0;
                  Long dbConnectionPoolUtilizationRate = NumberUtils.parseLong(dbConnectionPoolUtilization);
                  if (dbConnectionPoolUtilizationRate > Long.valueOf(configDbConnectionPool)) {
                     String alarmContent = I18nHelper.getMessage(
                        "T_CORE_SYSTEM_MONITOR_CONFIG.ALARM.SMS_CONTENT",
                        "数据库连接池",
                        systemInfoPortBuilder.toString(),
                        configDbConnectionPool,
                        dbConnectionPoolUtilizationRate.toString()
                     );
                     String emailSubject = "数据库连接池告警通知";
                     this.sendMessage(alarmContent, emailSubject);
                  }
               }
            } catch (Throwable var18) {
            }

            List<CoreLoginLogBean> onlineUserList = this.coreLoginLogService
               .selectListByFilter(
                  SearchFilter.instance().match("LOGOUTTIME", null).filter(MatchPattern.EQ).match("STATUS", "1").filter(MatchPattern.EQ), new Order[0]
               );
            Integer onlineUserQty = onlineUserList.size();
            String configOnlineUser = configMap.get("onlineUserThreshold");
            if (onlineUserQty != 0) {
               double onlineUserUtilization = (double)onlineUserQty.intValue() / ApplicationContextHelper.getLoginUserLimitation() * 100.0;
               Long onlineUserUtilizationRate = NumberUtils.parseLong(onlineUserUtilization);
               if (onlineUserUtilizationRate > Long.valueOf(configOnlineUser)) {
                  String alarmContent = I18nHelper.getMessage(
                     "T_CORE_SYSTEM_MONITOR_CONFIG.ALARM.SMS_CONTENT",
                     "用户并发数",
                     systemInfoPortBuilder.toString(),
                     configOnlineUser,
                     onlineUserUtilizationRate.toString()
                  );
                  String emailSubject = "用户并发数告警通知";
                  this.sendMessage(alarmContent, emailSubject);
               }
            }
         }
      );
   }

   private void sendMessage(String alarmContent, String emailSubject) {
      CoreEmailBean email = new CoreEmailBean();
      email.setEmailBody(alarmContent);
      email.setEmailSubject(emailSubject);
      List<CoreSystemMonitorConfigBean> configBeanList = this.coreSystemMonitorConfigService.selectAll();
      configBeanList = configBeanList.stream().filter(e -> Arrays.asList("notifierEmail", "notifierMobile").contains(e.getId())).collect(Collectors.toList());
      Map<String, String> configMap = configBeanList.stream().collect(HashMap::new, (map, item) -> {
         String var10000 = map.put(item.getId(), item.getConfigValue());
      }, HashMap::putAll);
      String notifierEmail = configMap.get("notifierEmail");
      if (!StringUtils.isEmpty(notifierEmail)) {
         List<CoreEmailReceipentBean> receipentBeanList = new ArrayList<>();
         List<String> addresseeList = Arrays.asList(notifierEmail.split(","));
         addresseeList.stream().forEach(l -> {
            CoreEmailReceipentBean receipentBean = new CoreEmailReceipentBean();
            receipentBean.setId(ApplicationContextHelper.getNextIdentity());
            receipentBean.setEmailId(ApplicationContextHelper.getNextIdentity());
            receipentBean.setReceipentEmail(l);
            receipentBean.setReceipentCategory("receipent");
            receipentBeanList.add(receipentBean);
         });
         List<CoreEmailReceipentBean> ccReceipentList = CollectionUtils.emptyList();
         List<CoreEmailFileBean> emailFileList = CollectionUtils.emptyList();
         this.coreEmailReceipentService.getDao().insert(receipentBeanList);
         this.coreEmailService.sendEmailForList(email, receipentBeanList, ccReceipentList, emailFileList);
      }

      String notifierMobile = configMap.get("notifierMobile");
      if (!StringUtils.isEmpty(notifierMobile)) {
         String[] phoneNumbers = notifierMobile.split(",");

         for (int i = 0; i < phoneNumbers.length; i++) {
            this.smsManager.send(phoneNumbers[i], alarmContent);
         }
      }
   }
}
