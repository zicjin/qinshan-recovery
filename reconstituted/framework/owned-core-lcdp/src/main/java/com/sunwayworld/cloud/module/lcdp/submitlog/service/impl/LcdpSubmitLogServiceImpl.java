package com.sunwayworld.cloud.module.lcdp.submitlog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.persistent.dao.LcdpSubmitLogDao;
import com.sunwayworld.cloud.module.lcdp.submitlog.service.LcdpSubmitLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpSubmitLogServiceImpl implements LcdpSubmitLogService {
   @Autowired
   private LcdpSubmitLogDao lcdpSubmitLogDao;
   @Lazy
   @Autowired
   private LcdpResourceVersionService resourceVersionService;

   public LcdpSubmitLogDao getDao() {
      return this.lcdpSubmitLogDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpSubmitLogBean lcdpSubmitLog = jsonWrapper.parseUnique(LcdpSubmitLogBean.class);
      lcdpSubmitLog.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(lcdpSubmitLog);
      return lcdpSubmitLog.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void submitResource(RestJsonWrapperBean wrapper) {
      List<LcdpSubmitLogBean> submitLogList = wrapper.parse(LcdpSubmitLogBean.class);
      LcdpSubmitLogBean submitLog = submitLogList.stream().findFirst().get();
      submitLog.setId(ApplicationContextHelper.getNextIdentity());
      this.resourceVersionService.submitResource(wrapper, submitLog.getId());
      this.getDao().insert(submitLog);
   }

   @Override
   public Page<LcdpResourceVersionBean> selectVersionPaginationByLogId(Long id, RestJsonWrapperBean wrapper) {
      return this.resourceVersionService.selectPaginationByFilter(SearchFilter.instance().match("LOGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public Page<LcdpResourceVersionBean> viewResource(RestJsonWrapperBean wrapper) {
      String logIdArrayStr = wrapper.getFilterValue("logId");
      if (StringUtils.isEmpty(logIdArrayStr)) {
         return null;
      } else {
         JSONArray array = JSONObject.parseArray(logIdArrayStr);
         List<String> LogIdStr = array.toJavaList(String.class);
         List<Long> logIdList = LogIdStr.stream().map(Long::valueOf).collect(Collectors.toList());
         MapperParameter parameter = wrapper.extractMapFilter();
         PageRowBounds rowBounds = wrapper.extractPageRowBounds();
         parameter.setRawQueries();
         parameter.setFilter(SearchFilter.instance().match("logId", logIdList).filter(MatchPattern.OR));
         if (!wrapper.getAuthorityList().isEmpty()) {
            wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
         }

         Page<LcdpResourceVersionBean> page = this.resourceVersionService.selectPagination(parameter, rowBounds);
         List<LcdpResourceVersionBean> resourceVersionList = page.getRows();
         List<LcdpResourceVersionBean> sortResourceVersionSet = resourceVersionList.stream()
            .sorted(Comparator.comparing(LcdpResourceVersionBean::getResourceCategory))
            .sorted(Comparator.comparing(LcdpResourceVersionBean::getResourceName))
            .collect(Collectors.toList());
         page.setRows(sortResourceVersionSet);
         return page;
      }
   }

   @Override
   public Page<LcdpSubmitLogBean> selectPagination(RestJsonWrapperBean wrapper) {
      String resourceId = wrapper.getParamValue("resourceId");
      if (StringUtils.isEmpty(resourceId)) {
         return LcdpSubmitLogService.super.selectPagination(wrapper);
      } else {
         List<LcdpResourceVersionBean> resourceVersionList = this.resourceVersionService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceId).filter(MatchPattern.EQ), new Order[0]);
         List<Long> logIdList = resourceVersionList.stream().map(LcdpResourceVersionBean::getLogId).collect(Collectors.toList());
         return this.selectPaginationByFilter(SearchFilter.instance().match("ID", logIdList).filter(MatchPattern.OR), wrapper);
      }
   }
}
