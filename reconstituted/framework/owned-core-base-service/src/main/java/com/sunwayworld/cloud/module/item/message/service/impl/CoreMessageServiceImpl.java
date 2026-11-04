package com.sunwayworld.cloud.module.item.message.service.impl;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageBean;
import com.sunwayworld.cloud.module.item.message.bean.CoreMessageReceipentBean;
import com.sunwayworld.cloud.module.item.message.persistent.dao.CoreMessageDao;
import com.sunwayworld.cloud.module.item.message.service.CoreMessageReceipentService;
import com.sunwayworld.cloud.module.item.message.service.CoreMessageService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.module.item.notification.bean.CoreNotificationDTO;
import com.sunwayworld.module.item.notification.helper.CoreNotificationHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class CoreMessageServiceImpl implements CoreMessageService {
   @Autowired
   private CoreMessageDao coreMessageDao;
   @Autowired
   @Lazy
   private CoreMessageReceipentService coreMessageReceipentService;
   @Autowired
   @Lazy
   private CoreUserService userService;

   public CoreMessageDao getDao() {
      return this.coreMessageDao;
   }

   @Transactional
   @Override
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreMessageBean coreMessageBean = wrapper.parseUnique(CoreMessageBean.class);
      coreMessageBean.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreMessageBean);
      return coreMessageBean.getId();
   }

   @Transactional
   @Override
   public void send(String subject, String content, String... receipents) {
      if (receipents != null && receipents.length != 0) {
         List<CoreUserBean> receipentList = this.userService.selectListByIds(Arrays.asList(receipents));
         CoreMessageBean message = new CoreMessageBean();
         message.setId(ApplicationContextHelper.getNextIdentity());
         message.setMessageSubject(subject);
         message.setMessageContent(content);
         message.setActivatedFlag("1");
         message.setActivatedById(LocalContextHelper.getLoginUserId());
         message.setActivatedByName(LocalContextHelper.getLoginUserName());
         message.setActivatedTime(LocalDateTime.now());
         this.getDao().insert(message);
         List<CoreMessageReceipentBean> messageReceipentList = new ArrayList<>();
         int qty = receipentList.size();
         List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);

         for (int i = 0; i < qty; i++) {
            CoreUserBean user = receipentList.get(i);
            CoreMessageReceipentBean receipent = new CoreMessageReceipentBean();
            receipent.setId(idList.get(i));
            receipent.setMessageId(message.getId());
            receipent.setReceipentId(user.getId());
            receipent.setReceipentName(user.getUserName());
            receipent.setReceivedFlag("0");
            messageReceipentList.add(receipent);
         }

         this.coreMessageReceipentService.getDao().insert(messageReceipentList);
      }
   }

   @Transactional
   @Override
   public void insertMessageReceipent(Long messageId, RestJsonWrapperBean wrapper) {
      List<CoreMessageReceipentBean> coreMessageReceipentList = wrapper.parse(CoreMessageReceipentBean.class);
      List<Long> idList = ApplicationContextHelper.getNextIdentityList(coreMessageReceipentList.size());

      for (int i = 0; i < idList.size(); i++) {
         CoreMessageReceipentBean coreMessageReceipentBean = coreMessageReceipentList.get(i);
         coreMessageReceipentBean.setId(idList.get(i));
         coreMessageReceipentBean.setMessageId(messageId);
      }

      this.coreMessageReceipentService.getDao().insert(coreMessageReceipentList);
   }

   @Transactional
   @Override
   public void deleteMessageReceipent(Long messageId, RestJsonWrapperBean wrapper) {
      this.coreMessageReceipentService.delete(wrapper);
   }

   @Transactional
   @Override
   public void receive(RestJsonWrapperBean wrapper) {
      List<CoreMessageReceipentBean> coreMessageReceipentList = wrapper.parse(CoreMessageReceipentBean.class);

      for (CoreMessageReceipentBean receipentBean : coreMessageReceipentList) {
         receipentBean.setReceivedFlag("1");
         receipentBean.setReceivedTime(LocalDateTime.now());
      }

      this.coreMessageReceipentService.getDao().update(coreMessageReceipentList, "RECEIVEDFLAG", "RECEIVEDTIME");
   }

   @Override
   public Page<CoreMessageBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setFilter(
         SearchFilter.instance()
            .match("ACTIVATEDFLAG", "1")
            .filter(MatchPattern.EQ)
            .match("RECEIPENTID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.EQ)
      );
      parameter.setSearchableQueries();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.coreMessageReceipentService.getDao().selectByCondition(parameter));
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, Collections.emptyList());
      } else {
         List<CoreMessageBean> itemList = page.getRows()
            .stream()
            .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreMessageBean.class))
            .collect(Collectors.toList());
         return new Pagination<>(page, itemList);
      }
   }

   @Transactional
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      CoreMessageService.super.activate(wrapper);
      List<Long> idList = wrapper.parseId(Long.class);
      List<CoreMessageBean> messageList = this.selectListByIds(idList);
      List<CoreMessageReceipentBean> messageReceipentList = this.coreMessageReceipentService
         .selectListByFilter(SearchFilter.instance().match("MESSAGEID", idList).filter(MatchPattern.OR), new Order[0]);
      List<CoreNotificationDTO> notificationList = new ArrayList<>();
      messageList.forEach(
         m -> {
            List<CoreMessageReceipentBean> receipentList = messageReceipentList.stream()
               .filter(mr -> m.getId().equals(mr.getMessageId()))
               .collect(Collectors.toList());
            receipentList.forEach(r -> {
               Map<String, Object> paramMap = new HashMap<>();
               paramMap.put("id", m.getId());
               paramMap.put("receipentId", "" + r.getId());
               paramMap.put("messageSender", m.getCreatedByName());
               paramMap.put("messageSubject", m.getMessageSubject());
               notificationList.add(CoreNotificationDTO.of("MESSAGE", ArrayUtils.asList(r.getReceipentId()), paramMap));
            });
         }
      );
      CoreNotificationHelper.sendNotificationList(notificationList);
   }
}
