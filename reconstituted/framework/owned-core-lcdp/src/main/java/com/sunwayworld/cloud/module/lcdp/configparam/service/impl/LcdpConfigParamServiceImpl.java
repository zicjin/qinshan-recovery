package com.sunwayworld.cloud.module.lcdp.configparam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.cloud.module.lcdp.configparam.persistent.dao.LcdpConfigParamDao;
import com.sunwayworld.cloud.module.lcdp.configparam.service.LcdpConfigParamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpConfigParamServiceImpl implements LcdpConfigParamService {
   @Autowired
   private LcdpConfigParamDao lcdpConfigParamDao;

   public LcdpConfigParamDao getDao() {
      return this.lcdpConfigParamDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      List<LcdpConfigParamBean> configParamList = jsonWrapper.parse(LcdpConfigParamBean.class);
      configParamList.stream().forEach(e -> e.setUserId(LocalContextHelper.getLoginUserId()));
      List<LcdpConfigParamBean> existConfigParamList = this.getDao()
         .selectList(configParamList, ArrayUtils.asList("USERID", "PARAMCODE"), CollectionUtils.emptyList(), new Order[0]);
      List<LcdpConfigParamBean> insertList = new ArrayList<>();
      List<LcdpConfigParamBean> updateList = new ArrayList<>();

      for (LcdpConfigParamBean configPram : configParamList) {
         LcdpConfigParamBean existConfigParam = existConfigParamList.stream()
            .filter(f -> ObjectUtils.equals(f.getParamCode(), configPram.getParamCode()))
            .findFirst()
            .orElse(null);
         if (existConfigParam == null) {
            configPram.setId(ApplicationContextHelper.getNextIdentity());
            insertList.add(configPram);
         } else {
            existConfigParam.setParamValue(configPram.getParamValue());
            updateList.add(existConfigParam);
         }
      }

      if (insertList.size() > 0) {
         this.getDao().insert(insertList);
      }

      if (updateList.size() > 0) {
         this.getDao().update(updateList, new String[]{"PARAMVALUE"});
      }

      return 1L;
   }

   @Override
   public JSONObject selectUserConfigParam(RestJsonWrapperBean wrapper) {
      List<LcdpConfigParamBean> configList = this.getDao().selectListByOneColumnValue(LocalContextHelper.getLoginUserId(), "USERID", new Order[0]);
      JSONObject obj = new JSONObject();
      configList.stream().forEach(e -> {
         obj.put(e.getParamCode(), e.getParamValue());
         obj.put("id", e.getId());
      });
      return obj;
   }

   @Override
   public String getCurrentDBMybatisMapperParam() {
      return this.selectFirstByFilter(SearchFilter.instance().match("paramCode", "currentDBMybatisMapper").filter(MatchPattern.EQ), new Order[0])
         .getParamValue();
   }
}
