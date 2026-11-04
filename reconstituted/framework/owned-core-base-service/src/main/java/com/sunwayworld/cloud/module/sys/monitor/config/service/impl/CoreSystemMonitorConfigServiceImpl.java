package com.sunwayworld.cloud.module.sys.monitor.config.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppRuntimeService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorReqService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerRuntimeService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorUserService;
import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.cloud.module.sys.monitor.config.persistent.dao.CoreSystemMonitorConfigDao;
import com.sunwayworld.cloud.module.sys.monitor.config.service.CoreSystemMonitorConfigService;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.service.CoreSystemMonitorErrorLogService;
import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.cloud.module.sys.monitor.pm.service.CoreSystemMonitorSqlService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemMonitorConfigServiceImpl implements CoreSystemMonitorConfigService {
   @Autowired
   private CoreSystemMonitorConfigDao coreSystemMonitorConfigDao;
   @Autowired
   private CoreSystemMonitorErrorLogService coreSystemMonitorErrorLogService;
   @Autowired
   private CoreSystemMonitorSqlService coreSystemMonitorSqlService;
   @Autowired
   private CoreMonitorAppRuntimeService coreMonitorAppRuntimeService;
   @Autowired
   private CoreMonitorServerRuntimeService coreMonitorServerRuntimeService;
   @Autowired
   private CoreMonitorReqService coreMonitorReqService;
   @Autowired
   private CoreMonitorUserService coreMonitorUserService;
   @Autowired
   private DaoMapper daoMapper;

   public CoreSystemMonitorConfigDao getDao() {
      return this.coreSystemMonitorConfigDao;
   }

   @Cacheable(
      value = {"T_CORE_SYSTEM_MONITOR_CONFIG"},
      key = "'ALL'"
   )
   @Override
   public List<CoreSystemMonitorConfigBean> selectAll() {
      return this.getDao().selectAll();
   }

   @Scheduled(
      cron = " 0 0 2 * * ?"
   )
   @Override
   public void scheduleAutoClean() {
      String validAutoClean = ApplicationContextHelper.getConstantValue("AUTO_CLEAN");
      if (!"0".equals(validAutoClean)) {
         List<CoreSystemMonitorConfigBean> configBeanList = this.getDao().selectListByOneColumnValue("autoClean", "configCategory", new Order[0]);
         Map<String, String> configMap = configBeanList.stream().collect(HashMap::new, (map, item) -> {
            String var10000 = map.put(item.getId(), item.getConfigValue());
         }, HashMap::putAll);
         LocalDateTime dateTime = LocalDateTime.now();
         if (!StringUtils.isEmpty(configMap.get("errorLog"))) {
            String errorLog = configMap.get("errorLog");
            Date errorLogTime = Date.from(dateTime.minusDays(Long.valueOf(errorLog)).atZone(ZoneId.systemDefault()).toInstant());
            List<CoreSystemMonitorErrorLogBean> errorLogBeanList = this.coreSystemMonitorErrorLogService
               .selectListByFilter(SearchFilter.instance().match("LOGTIME", errorLogTime).filter(MatchPattern.TLOE), new Order[0]);
            this.coreSystemMonitorErrorLogService.getDao().deleteBy(errorLogBeanList);
         }

         if (!StringUtils.isEmpty(configMap.get("sqlMonitor"))) {
            String sqlMonitor = configMap.get("sqlMonitor");
            Date sqlMonitorTime = Date.from(dateTime.minusDays(Long.valueOf(sqlMonitor)).atZone(ZoneId.systemDefault()).toInstant());
            List<CoreSystemMonitorSqlBean> sqlBeanList = this.coreSystemMonitorSqlService
               .selectListByFilter(SearchFilter.instance().match("EXECUTETIME", sqlMonitorTime).filter(MatchPattern.TLOE), new Order[0]);
            this.coreSystemMonitorSqlService.getDao().deleteBy(sqlBeanList);
         }

         if (!StringUtils.isEmpty(configMap.get("serverMonitor"))) {
            String serverMonitor = configMap.get("serverMonitor");
            Date serverMonitorTime = Date.from(dateTime.minusDays(Long.valueOf(serverMonitor)).atZone(ZoneId.systemDefault()).toInstant());
            List<CoreMonitorAppRuntimeBean> appRuntimeList = this.coreMonitorAppRuntimeService
               .selectListByFilter(SearchFilter.instance().match("CREATEDTIME", serverMonitorTime).filter(MatchPattern.TLOE), new Order[0]);
            ListChunkIterator<CoreMonitorAppRuntimeBean> appRunTimeChunkIterator = ListChunkIterator.of(appRuntimeList, 999);

            while (appRunTimeChunkIterator.hasNext()) {
               this.daoMapper
                  .batchDeleteById(
                     this.coreMonitorAppRuntimeService.getDao().getEntityContext(),
                     appRunTimeChunkIterator.nextChunk().stream().map(CoreMonitorAppRuntimeBean::getId).collect(Collectors.toList())
                  );
            }

            List<CoreMonitorServerRuntimeBean> serverRuntimeList = this.coreMonitorServerRuntimeService
               .selectListByFilter(SearchFilter.instance().match("CREATEDTIME", serverMonitorTime).filter(MatchPattern.TLOE), new Order[0]);
            ListChunkIterator<CoreMonitorServerRuntimeBean> serverRuntimeChunkIterator = ListChunkIterator.of(serverRuntimeList, 999);

            while (serverRuntimeChunkIterator.hasNext()) {
               this.daoMapper
                  .batchDeleteById(
                     this.coreMonitorServerRuntimeService.getDao().getEntityContext(),
                     serverRuntimeChunkIterator.nextChunk().stream().map(CoreMonitorServerRuntimeBean::getId).collect(Collectors.toList())
                  );
            }

            List<CoreMonitorReqBean> reqList = this.coreMonitorReqService
               .selectListByFilter(SearchFilter.instance().match("CREATEDTIME", serverMonitorTime).filter(MatchPattern.TLOE), new Order[0]);
            ListChunkIterator<CoreMonitorReqBean> reqRuntimeChunkIterator = ListChunkIterator.of(reqList, 999);

            while (reqRuntimeChunkIterator.hasNext()) {
               this.daoMapper
                  .batchDeleteById(
                     this.coreMonitorReqService.getDao().getEntityContext(),
                     reqRuntimeChunkIterator.nextChunk().stream().map(CoreMonitorReqBean::getId).collect(Collectors.toList())
                  );
            }

            List<CoreMonitorUserBean> userList = this.coreMonitorUserService
               .selectListByFilter(SearchFilter.instance().match("CREATEDTIME", serverMonitorTime).filter(MatchPattern.TLOE), new Order[0]);
            ListChunkIterator<CoreMonitorUserBean> userChunkIterator = ListChunkIterator.of(userList, 999);

            while (userChunkIterator.hasNext()) {
               this.daoMapper
                  .batchDeleteById(
                     this.coreMonitorReqService.getDao().getEntityContext(),
                     userChunkIterator.nextChunk().stream().map(CoreMonitorUserBean::getId).collect(Collectors.toList())
                  );
            }
         }
      }
   }
}
