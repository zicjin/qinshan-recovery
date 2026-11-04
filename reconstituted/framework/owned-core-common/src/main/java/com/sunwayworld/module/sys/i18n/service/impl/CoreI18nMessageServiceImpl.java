package com.sunwayworld.module.sys.i18n.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nMessageDao;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreI18nMessageServiceImpl implements CoreI18nMessageService {
   @Autowired
   private CoreI18nMessageDao dao;
   @Lazy
   @Autowired
   private CoreI18nConfigService configService;
   @Lazy
   @Autowired
   private CoreI18nService service;
   @Lazy
   @Autowired
   private CoreI18nMessageService proxy;

   public CoreI18nMessageDao getDao() {
      return this.dao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void initMessagesByI18nId(Long i18nId) {
      List<CoreI18nConfigBean> configList = this.configService.selectAll();
      CoreI18nMessageBean filter = new CoreI18nMessageBean();
      filter.setI18nId(i18nId);
      List<CoreI18nMessageBean> messageList = this.getDao().selectList(filter, new Order[0]);
      List<CoreI18nConfigBean> missingConfigList = configList.stream()
         .filter(c -> messageList.stream().noneMatch(m -> c.getId().equals(m.getI18nConfigId())))
         .collect(Collectors.toList());
      if (!missingConfigList.isEmpty()) {
         List<CoreI18nMessageBean> insertMessageList = new ArrayList<>();
         CoreI18nBean i18n = this.service.selectById(i18nId);

         for (CoreI18nConfigBean config : missingConfigList) {
            CoreI18nMessageBean message = new CoreI18nMessageBean();
            message.setId(ApplicationContextHelper.getNextIdentity());
            message.setI18nId(i18nId);
            message.setI18nConfigId(config.getId());
            message.setMessage(i18n.getDefaultMessage());
            insertMessageList.add(message);
         }

         this.getDao().insert(insertMessageList);
      }
   }

   @Override
   public Map<String, Map<String, String>> selectMessageMapByLocaleGrouping() {
      List<CoreI18nMessageBean> messageList = this.selectList(new MapperParameter());
      Map<String, Map<String, String>> map = new HashMap<>();

      for (CoreI18nMessageBean message : messageList) {
         String localeId = message.getI18nConfigId().toLowerCase();
         Map<String, String> i18nMap = map.get(localeId);
         if (i18nMap == null) {
            i18nMap = new HashMap<>();
            map.put(localeId, i18nMap);
         }

         i18nMap.put(message.getExt$Item("code"), message.getMessage());
      }

      return map;
   }
}
