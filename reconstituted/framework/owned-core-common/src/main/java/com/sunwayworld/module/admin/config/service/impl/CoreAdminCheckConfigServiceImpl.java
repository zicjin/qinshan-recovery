package com.sunwayworld.module.admin.config.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigBean;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigDTO;
import com.sunwayworld.module.admin.config.dao.CoreAdminCheckConfigDao;
import com.sunwayworld.module.admin.config.service.CoreAdminCheckConfigService;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdminCheckConfigServiceImpl implements CoreAdminCheckConfigService {
   @Autowired
   private CoreAdminCheckConfigDao dao;

   public CoreAdminCheckConfigDao getDao() {
      return this.dao;
   }

   @Cacheable(
      value = {"T_CORE_ADMIN_CHECK_CONFIG"},
      key = "'ALL'"
   )
   @Override
   public List<CoreAdminCheckConfigDTO> selectAllConfig() {
      List<CoreAdminCheckConfigBean> checkConfigList = CoreAdminCheckConfigService.super.selectAll();
      return checkConfigList.stream().map(e -> {
         CoreAdminCheckConfigDTO checkConfigDTO = new CoreAdminCheckConfigDTO();
         BeanUtils.copyProperties(e, checkConfigDTO);
         checkConfigDTO.setCategory(e.getId());
         checkConfigDTO.setValueField("code");
         checkConfigDTO.setTextField("codeName");
         checkConfigDTO.setGroupField("1".equals(e.getGroupDisplay()) ? "groupName" : null);
         return checkConfigDTO;
      }).collect(Collectors.toList());
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public String insert(RestJsonWrapperBean wrapper) {
      CoreAdminCheckConfigBean config = wrapper.parseUnique(CoreAdminCheckConfigBean.class);
      this.getDao().insert(config);
      return config.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertByCheckCode(CoreCheckCodeBean coreCheckCode) {
      String codeCategory = coreCheckCode.getCodeCategory();
      CoreAdminCheckConfigBean existConfig = this.getDao().selectByIdIfPresent(codeCategory);
      if (existConfig == null) {
         CoreAdminCheckConfigBean config = new CoreAdminCheckConfigBean();
         config.setId(codeCategory);
         config.setName(codeCategory);
         config.setUrl("/secure/core/module/sys/check-codes/queries/checkable?codeCategory=" + codeCategory);
         config.setGroupDisplay("0");
         config.setCacheable("1");
         config.setDisplayOther("0");
         config.setOtherRequired("0");
         this.getDao().insert(config);
      }
   }
}
