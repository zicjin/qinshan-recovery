package com.sunwayworld.module.sys.i18n.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nDao;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreI18nServiceImpl implements CoreI18nService {
   @Autowired
   private CoreI18nDao dao;
   @Autowired
   private CoreI18nMessageService messageService;
   @Autowired
   private CoreI18nConfigService configService;

   public CoreI18nDao getDao() {
      return this.dao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreI18nBean i18n = wrapper.parseUnique(CoreI18nBean.class);
      i18n.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(i18n);
      List<CoreI18nConfigBean> configList = this.configService.selectAll();
      List<CoreI18nMessageBean> messageList = new ArrayList<>();

      for (CoreI18nConfigBean config : configList) {
         CoreI18nMessageBean message = new CoreI18nMessageBean();
         message.setId(ApplicationContextHelper.getNextIdentity());
         message.setI18nConfigId(config.getId());
         message.setI18nId(i18n.getId());
         message.setMessage(i18n.getDefaultMessage());
         messageList.add(message);
      }

      this.messageService.getDao().insert(messageList);
      return i18n.getId();
   }
}
