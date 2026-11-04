package com.sunwayworld.cloud.module.item.customquery.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryConditionDao;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryConditionService;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryFieldService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomQueryConditionServiceImpl implements CoreCustomQueryConditionService {
   @Autowired
   private CoreCustomQueryConditionDao coreCustomQueryConditionDao;
   @Autowired
   private CoreCustomQueryFieldService coreCustomQueryFieldService;

   public CoreCustomQueryConditionDao getDao() {
      return this.coreCustomQueryConditionDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCustomQueryConditionBean coreCustomQueryCondition = jsonWrapper.parseUnique(CoreCustomQueryConditionBean.class);
      coreCustomQueryCondition.setId(ApplicationContextHelper.getNextIdentity());
      coreCustomQueryCondition.setOrderNo(coreCustomQueryCondition.getId());
      this.getDao().insert(coreCustomQueryCondition);
      return coreCustomQueryCondition.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void addQueryConditions(RestJsonWrapperBean wrapper) {
      List<CoreCustomQueryConditionBean> conditionBeanList = wrapper.parse(CoreCustomQueryConditionBean.class);
      conditionBeanList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setOrderNo(e.getId());
         if (StringUtils.isBlank(e.getFrReportDsName())) {
            e.setConditionCategory("string");
         }
      });
      this.getDao().insert(conditionBeanList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delQueryConditions(RestJsonWrapperBean wrapper) {
      List<String> idList = wrapper.getBodyList().get(0);
      List<Map<String, Object>> idMapList = new ArrayList<>();
      idList.forEach(e -> {
         Map<String, Object> map = (Map<String, Object>)JSON.parse(e);
         idMapList.add(map);
      });
      List<Long> conditionIds = idMapList.stream().map(e -> (Long)e.get("id")).collect(Collectors.toList());
      List<CoreCustomQueryConditionBean> conditionBeanList = this.getDao().selectListByIds(conditionIds, new Order[0]);
      List<String> fieldNameList = conditionBeanList.stream().map(e -> e.getFieldName()).collect(Collectors.toList());
      List<CoreCustomQueryFieldBean> fieldList = this.coreCustomQueryFieldService
         .selectListByFilter(
            SearchFilter.instance()
               .match("FIELDNAME", fieldNameList)
               .filter(MatchPattern.OR)
               .match("CUSTOMQUERYID", conditionBeanList.get(0).getCustomQueryId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      fieldList.forEach(e -> e.setDefaultCondition("0"));
      this.delete(wrapper);
      this.coreCustomQueryFieldService.getDao().update(fieldList, "DEFAULTCONDITION");
   }
}
