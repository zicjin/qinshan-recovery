package com.sunwayworld.cloud.module.equipt.monitorconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorConfigBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorLogBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.CoreEquiptMonitorConfigDao;
import com.sunwayworld.cloud.module.equipt.monitorconfig.service.CoreEquiptMonitorConfigService;
import com.sunwayworld.cloud.module.equipt.monitorconfig.service.CoreEquiptMonitorLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.NotImplementedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEquiptMonitorConfigServiceImpl implements CoreEquiptMonitorConfigService {
   @Autowired
   private CoreEquiptMonitorConfigDao coreEquiptMonitorConfigDao;
   @Autowired
   private CoreEquiptMonitorLogService coreEquiptMonitorLogService;

   public CoreEquiptMonitorConfigDao getDao() {
      return this.coreEquiptMonitorConfigDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreEquiptMonitorConfigBean coreEquiptMonitorConfig = jsonWrapper.parseUnique(CoreEquiptMonitorConfigBean.class);
      coreEquiptMonitorConfig.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreEquiptMonitorConfig);
      return coreEquiptMonitorConfig.getId();
   }

   @Override
   public Page<CoreEquiptMonitorLogBean> selectMonitorLogPaginationByEquiptMonitorConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreEquiptMonitorLogService.selectPaginationByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public String selectLastWorkStatusByCode(String code) {
      CoreEquiptMonitorConfigBean equiptMonitorConfigBean = this.selectFirstByFilter(
         SearchFilter.instance().match("CODE", code).filter(MatchPattern.EQ), new Order[0]
      );
      CoreEquiptMonitorLogBean logBean = this.coreEquiptMonitorLogService
         .selectFirstByFilter(
            SearchFilter.instance().match("CONFIGID", equiptMonitorConfigBean.getId()).filter(MatchPattern.EQ), new Order[]{Order.desc("CREATEDTIME")}
         );
      return logBean != null && logBean.getOnTime() != null && logBean.getDownTime() == null ? "1" : "0";
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertMonitorLogByCode(String code) {
      CoreEquiptMonitorConfigBean equiptMonitorConfigBean = this.selectFirstByFilter(
         SearchFilter.instance().match("CODE", code).filter(MatchPattern.EQ), new Order[0]
      );
      if (equiptMonitorConfigBean != null) {
         CoreEquiptMonitorLogBean coreEquiptMonitorLog = new CoreEquiptMonitorLogBean();
         coreEquiptMonitorLog.setId(ApplicationContextHelper.getNextIdentity());
         coreEquiptMonitorLog.setConfigId(equiptMonitorConfigBean.getId());
         coreEquiptMonitorLog.setOnTime(LocalDateTime.now());
         this.coreEquiptMonitorLogService.getDao().insert(coreEquiptMonitorLog);
         return coreEquiptMonitorLog.getId();
      } else {
         return null;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void updateMonitorLogByCode(String code) {
      CoreEquiptMonitorConfigBean equiptMonitorConfigBean = this.selectFirstByFilter(
         SearchFilter.instance().match("CODE", code).filter(MatchPattern.EQ), new Order[0]
      );
      CoreEquiptMonitorLogBean logBean = this.coreEquiptMonitorLogService
         .selectFirstByFilter(
            SearchFilter.instance().match("CONFIGID", equiptMonitorConfigBean.getId()).filter(MatchPattern.EQ), new Order[]{Order.desc("CREATEDTIME")}
         );
      if (logBean != null) {
         logBean.setDownTime(LocalDateTime.now());
         this.coreEquiptMonitorLogService.getDao().update(logBean, "DOWNTIME");
      }
   }

   @Override
   public void updateEquiptWorkStatus(String code) {
      throw new NotImplementedException();
   }
}
