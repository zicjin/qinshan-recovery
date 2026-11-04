package com.sunwayworld.cloud.module.lcdp.config.service.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalCompConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.dao.LcdpGlobalCompConfigDao;
import com.sunwayworld.cloud.module.lcdp.config.service.LcdpGlobalCompConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpGlobalCompConfigServiceImpl implements LcdpGlobalCompConfigService {
   @Autowired
   private LcdpGlobalCompConfigDao lcdpGlobalCompConfigDao;

   public LcdpGlobalCompConfigDao getDao() {
      return this.lcdpGlobalCompConfigDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      List<LcdpGlobalCompConfigBean> lcdpGlobalCompConfigList = jsonWrapper.parse(LcdpGlobalCompConfigBean.class);
      List<LcdpGlobalCompConfigBean> allCompConfigList = this.selectAll();
      this.getDao().deleteBy(allCompConfigList, new String[0]);
      lcdpGlobalCompConfigList.forEach(config -> config.setId(ApplicationContextHelper.getNextIdentity()));
      this.getDao().insert(lcdpGlobalCompConfigList);
      return lcdpGlobalCompConfigList.get(0).getId();
   }

   @Override
   public Map<String, List<LcdpGlobalCompConfigBean>> selectConfigList() {
      List<LcdpGlobalCompConfigBean> lcdpGlobalCompConfigList = this.selectAll();
      return lcdpGlobalCompConfigList.stream().collect(Collectors.groupingBy(LcdpGlobalCompConfigBean::getCompCategory));
   }
}
