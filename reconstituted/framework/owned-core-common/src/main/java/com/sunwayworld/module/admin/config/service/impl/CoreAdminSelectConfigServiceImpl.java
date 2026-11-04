package com.sunwayworld.module.admin.config.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.admin.config.dao.CoreAdminSelectConfigDao;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdminSelectConfigServiceImpl implements CoreAdminSelectConfigService {
   @Autowired
   private CoreAdminSelectConfigDao dao;
   @Lazy
   @Autowired
   private CoreAdminSelectConfigService proxyInstance;
   @Lazy
   @Autowired
   private CoreCodeCategoryService coreCodeCategoryService;
   @Autowired
   private CoreConstantService coreConstantService;

   public CoreAdminSelectConfigDao getDao() {
      return this.dao;
   }

   @Cacheable(
      value = {"T_CORE_ADMIN_SELECT_CONFIG"},
      key = "'ALL'"
   )
   @Override
   public List<CoreAdminSelectConfigBean> selectAll() {
      return CoreAdminSelectConfigService.super.selectAll()
         .stream()
         .collect(
            Collectors.collectingAndThen(
               Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CoreAdminSelectConfigBean::getCategory))), ArrayList::new
            )
         );
   }

   @Override
   public List<CoreAdminSelectConfigBean> selectSelectableList() {
      return this.proxyInstance.selectAll();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreAdminSelectConfigBean config = wrapper.parseUnique(CoreAdminSelectConfigBean.class);
      config.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(config);
      return config.getId();
   }

   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Transactional
   @Override
   public void insertByCodeCategory(CoreCodeCategoryBean codeCategory) {
      CoreAdminSelectConfigBean adminSelectConfig = new CoreAdminSelectConfigBean();
      adminSelectConfig.setId(ApplicationContextHelper.getNextIdentity());
      adminSelectConfig.setCategory(codeCategory.getId());
      adminSelectConfig.setCacheable("1");
      adminSelectConfig.setName(codeCategory.getCodeCategoryName());
      adminSelectConfig.setUrl("/secure/core/module/sys/codes/queries/selectable?codeCategoryId=" + codeCategory.getId());
      adminSelectConfig.setTextField("codeName");
      adminSelectConfig.setValueField("code");
      this.getDao().insert(adminSelectConfig);
   }

   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Transactional
   @Override
   public void supplementSelectConfig() {
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("CORE_SELECT_CONFIG_SUPPLEMENT");
      if (queryCoreConstant == null) {
         List<CoreAdminSelectConfigBean> coreAdminSelectConfigList = this.selectAll();
         List<String> codeCategoryIdList = coreAdminSelectConfigList.stream().map(config -> {
            try {
               URL url = new URL("http://localhost:80" + config.getUrl());
               String query = url.getQuery();
               if (StringUtils.isEmpty(query)) {
                  return "";
               } else {
                  String decodedQuery = URLDecoder.decode(query, "UTF-8");
                  Pattern pattern = Pattern.compile("([^&=]+)=([^&=]*)");
                  Matcher matcher = pattern.matcher(decodedQuery);

                  while (matcher.find()) {
                     if (matcher.group(1).equals("codeCategoryId")) {
                        return matcher.group(2);
                     }
                  }

                  return "";
               }
            } catch (Exception var6x) {
               return "";
            }
         }).filter(categoryId -> !StringUtils.isEmpty(categoryId)).collect(Collectors.toList());
         List<CoreCodeCategoryBean> coreCodeCategoryList = this.coreCodeCategoryService.selectAll();
         coreCodeCategoryList.removeIf(category -> codeCategoryIdList.contains(category.getId()));
         if (!coreCodeCategoryList.isEmpty()) {
            List<CoreAdminSelectConfigBean> insertAdminSelectConfigList = coreCodeCategoryList.stream().map(codeCategory -> {
               CoreAdminSelectConfigBean adminSelectConfig = new CoreAdminSelectConfigBean();
               adminSelectConfig.setId(ApplicationContextHelper.getNextIdentity());
               adminSelectConfig.setCategory(codeCategory.getId());
               adminSelectConfig.setCacheable("1");
               adminSelectConfig.setName(codeCategory.getCodeCategoryName());
               adminSelectConfig.setUrl("/secure/core/module/sys/codes/queries/selectable?codeCategoryId=" + codeCategory.getId());
               adminSelectConfig.setTextField("codeName");
               adminSelectConfig.setValueField("code");
               return adminSelectConfig;
            }).collect(Collectors.toList());
            List<String> currentCategoryList = coreAdminSelectConfigList.stream().map(CoreAdminSelectConfigBean::getCategory).collect(Collectors.toList());
            insertAdminSelectConfigList.removeIf(config -> currentCategoryList.contains(config.getCategory()));
            this.getDao().insert(insertAdminSelectConfigList);
            CoreConstantBean coreConstant = new CoreConstantBean();
            coreConstant.setId("CORE_SELECT_CONFIG_SUPPLEMENT");
            coreConstant.setConstantValue("1");
            coreConstant.setConstantDesc("低代码平台下拉框数据处理");
            coreConstant.setClassfication("系统");
            this.coreConstantService.getDao().insert(coreConstant);
         }
      }
   }
}
