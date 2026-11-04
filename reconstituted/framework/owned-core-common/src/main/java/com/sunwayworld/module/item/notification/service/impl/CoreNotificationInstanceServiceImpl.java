package com.sunwayworld.module.item.notification.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.notification.bean.CoreNotificationDTO;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceToBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationMessageDTO;
import com.sunwayworld.module.item.notification.bean.CoreNotificationMessagePageDTO;
import com.sunwayworld.module.item.notification.bean.CoreNotificationTemplateBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceDao;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceFileService;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceService;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceToService;
import com.sunwayworld.module.item.notification.service.CoreNotificationTemplateService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreNotificationInstanceServiceImpl implements CoreNotificationInstanceService {
   @Autowired
   private CoreNotificationInstanceDao instanceDao;
   @Autowired
   private CoreNotificationTemplateService templateService;
   @Autowired
   private CoreNotificationInstanceToService instanceToService;
   @Autowired
   private CoreNotificationInstanceFileService instanceFileService;

   public CoreNotificationInstanceDao getDao() {
      return this.instanceDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertNotificationList(List<CoreNotificationDTO> notificationList) {
      if (!CollectionUtils.isEmpty(notificationList)) {
         CoreNotificationTemplateBean template = this.templateService.selectById(notificationList.get(0).getTemplateId());
         List<CoreNotificationInstanceBean> insertInstanceList = new ArrayList<>();
         List<CoreNotificationInstanceToBean> insertInstanceToList = new ArrayList<>();
         List<CoreNotificationInstanceFileBean> insertInstanceFileList = new ArrayList<>();
         notificationList.forEach(n -> {
            CoreNotificationInstanceBean instance = new CoreNotificationInstanceBean();
            instance.setId(ApplicationContextHelper.getNextIdentity());
            instance.setTemplateId(template.getId());
            instance.setViewMethod(template.getViewMethod());
            instance.setMenuId(template.getMenuId());
            instance.setMenuName(template.getMenuName());
            instance.setPageUrl(StringUtils.replaceBetween(template.getPageUrl(), "${", "}", n.getParamMap()));
            instance.setTitle(StringUtils.replaceBetween(template.getTitle(), "${", "}", n.getParamMap()));
            instance.setContent(StringUtils.replaceBetween(template.getContent(), "${", "}", n.getParamMap()));
            instance.setParam(JSON.toJSONString(n.getParamMap(), SerializerFeature.WriteMapNullValue));
            insertInstanceList.add(instance);
            List<CoreNotificationInstanceToBean> toList = n.getNotifiedIdList().stream().map(t -> {
               CoreNotificationInstanceToBean to = new CoreNotificationInstanceToBean();
               to.setId(ApplicationContextHelper.getNextIdentity());
               to.setInstId(instance.getId());
               to.setNotifiedId(t);
               to.setRead("0");
               to.setIgnore("0");
               return to;
            }).collect(Collectors.toList());
            insertInstanceToList.addAll(toList);
            if (!CollectionUtils.isEmpty(n.getFilePairList())) {
               List<CoreNotificationInstanceFileBean> fileList = n.getFilePairList().stream().map(f -> {
                  CoreNotificationInstanceFileBean file = new CoreNotificationInstanceFileBean();
                  file.setId(ApplicationContextHelper.getNextIdentity());
                  file.setInstId(instance.getId());
                  file.setFileName(f.getFirst());
                  file.setFileDownloadUrl(f.getSecond());
                  return file;
               }).collect(Collectors.toList());
               insertInstanceFileList.addAll(fileList);
            }
         });
         this.getDao().insert(insertInstanceList);
         this.instanceToService.getDao().insert(insertInstanceToList);
         this.instanceFileService.getDao().insert(insertInstanceFileList);
      }
   }

   @Override
   public CoreNotificationMessagePageDTO selectNotificationMessageList(RestJsonWrapperBean wrapper) {
      Page<CoreNotificationInstanceToBean> notificationPage = this.instanceToService.selectPagination(wrapper);
      List<CoreNotificationInstanceToBean> notificationList = notificationPage.getRows();
      CoreNotificationMessagePageDTO messageDTO = new CoreNotificationMessagePageDTO();
      if (notificationList.isEmpty()) {
         messageDTO.setNotificationMessageList(CollectionUtils.emptyList());
         messageDTO.setTotal(0);
         return messageDTO;
      } else {
         List<Long> notificationIdList = notificationList.stream().map(CoreNotificationInstanceToBean::getInstId).collect(Collectors.toList());
         List<CoreNotificationInstanceFileBean> fileList = this.instanceFileService
            .selectListByFilter(SearchFilter.instance().match("INSTID", notificationIdList).filter(MatchPattern.OR), new Order[0]);
         List<CoreNotificationMessageDTO> messageList = notificationList.stream().map(n -> {
            CoreNotificationMessageDTO message = new CoreNotificationMessageDTO();
            message.setId(n.getId());
            message.setTitle(n.getExt$Item("title"));
            message.setContent(n.getExt$Item("content"));
            message.setViewMethod(n.getExt$Item("viewmethod"));
            message.setMenuId(n.getExt$Item("menuid"));
            message.setMenuName(n.getExt$Item("menuname"));
            message.setPageUrl(n.getExt$Item("pageurl"));
            message.setRead(n.getExt$Item("read"));
            message.setCreatedTime(DateTimeUtils.parseLocalDateTime(n.getExt$Item("createdtime")));
            message.setParam(n.getExt$Item("param"));
            message.setFileList(fileList.stream().filter(f -> f.getInstId().equals(n.getInstId())).collect(Collectors.toList()));
            return message;
         }).collect(Collectors.toList());
         messageDTO.setNotificationMessageList(messageList);
         messageDTO.setTotal(notificationPage.getTotal());
         return messageDTO;
      }
   }

   @Override
   public Page<CoreNotificationInstanceFileBean> selectNotificationFilePagination(Long id, RestJsonWrapperBean wrapper) {
      return this.instanceFileService.selectPaginationByFilter(SearchFilter.instance().match("INSTID", id).filter(MatchPattern.EQ), wrapper);
   }
}
