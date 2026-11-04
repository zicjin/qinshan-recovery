package com.sunwayworld.cloud.module.lcdp.submitlogtmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.bean.LcdpSubmitLogTmplBean;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.persistent.dao.LcdpSubmitLogTmplDao;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.service.LcdpSubmitLogTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpSubmitLogTmplServiceImpl implements LcdpSubmitLogTmplService {
   @Autowired
   private LcdpSubmitLogTmplDao lcdpSubmitLogTmplDao;

   public LcdpSubmitLogTmplDao getDao() {
      return this.lcdpSubmitLogTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpSubmitLogTmplBean lcdpSubmitLogTmpl = jsonWrapper.parseUnique(LcdpSubmitLogTmplBean.class);
      lcdpSubmitLogTmpl.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(lcdpSubmitLogTmpl);
      return lcdpSubmitLogTmpl.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      List<LcdpSubmitLogTmplBean> list = this.selectListByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ), new Order[0]);
      if (!list.isEmpty()) {
         list.stream().forEach(e -> e.setActivatedFlag("0"));
         this.getDao().update(list, new String[]{"ACTIVATEDFLAG"});
      }

      LcdpSubmitLogTmplService.super.activate(wrapper);
   }
}
