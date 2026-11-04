package com.sunwayworld.cloud.module.im.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageDTO;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImMessageDao;
import com.sunwayworld.cloud.module.im.service.CoreImContactsService;
import com.sunwayworld.cloud.module.im.service.CoreImMessageService;
import com.sunwayworld.cloud.module.im.support.ImHelper;
import com.sunwayworld.cloud.module.im.support.ImWebSocket;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreImMessageServiceImpl implements CoreImMessageService {
   @Autowired
   private CoreImMessageDao coreImMessageDao;
   @Autowired
   private CoreImContactsService coreImContactsService;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;
   @Autowired
   private ImWebSocket imWebSocket;
   private static final Logger logger = LogManager.getLogger(CoreImMessageService.class);
   private static final String IMG_OPEN_TAG = "[!img=";
   private static final String FILE_OPEN_TAG = "[!file=";
   private static final String IMG_FILE_CLOSE_TAG = "]";
   private static final String FILE_PROP_SEPARATOR = "__$$__";

   public CoreImMessageDao getDao() {
      return this.coreImMessageDao;
   }

   @Transactional
   @Override
   public void insert(CoreImMessageBean message) {
      message.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(message);
      this.coreImContactsService.insertByMessage(message);
   }

   @Override
   public Page<CoreImMessageBean> selectMessagePaginationByContactsId(String contactsId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.put("contactsId", contactsId);
      Page<CoreImMessageBean> messagePage = this.selectPagination(parameter, rowBounds);
      List<CoreImMessageBean> messageList = messagePage.getRows();
      if (messageList.isEmpty()) {
         return messagePage;
      } else {
         List<CoreFileBean> fileList = this.getFileListByMessage(messageList);
         if (fileList.isEmpty()) {
            return messagePage;
         } else {
            messageList.forEach(
               e -> {
                  List<CoreFileBean> subFileList = fileList.stream()
                     .filter(f -> (this.getDao().getTable() + "$" + e.getId()).equals(f.getTargetId()))
                     .collect(Collectors.toList());
                  if (!subFileList.isEmpty()) {
                     e.setContent(this.buildImMessageContentWithFile(e.getContent(), subFileList));
                  }
               }
            );
            messagePage.setRows(messageList);
            return messagePage;
         }
      }
   }

   @Transactional
   @Override
   public void read(String contactsId) {
      CoreImMessageBean condition = new CoreImMessageBean();
      condition.setReceiveUserId(LocalContextHelper.getLoginUserId());
      condition.setSendUserId(contactsId);
      condition.setOfflineFlag("1");
      List<CoreImMessageBean> messageList = this.getDao().selectList(condition, new Order[0]);
      messageList.forEach(e -> e.setOfflineFlag("0"));
      this.getDao().update(messageList, new String[]{"OFFLINEFLAG"});
   }

   @Override
   public void topMessage(String userId) {
      this.coreImContactsService.topMessage(userId);
   }

   @Override
   public void deleteContacts(String userId) {
      this.coreImContactsService.deleteContacts(userId);
   }

   @Override
   public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean wrapper) {
      return this.coreImContactsService.selectRecentContactsPagination(wrapper);
   }

   @Transactional
   @Override
   public Boolean insertMessage(RestJsonWrapperBean wrapper) {
      try {
         GikamConcurrentLocker.blockRun("IM_SERVICE_NAME::IM_SERVICE_KEY", () -> {
            CoreImMessageBean message = wrapper.parseUnique(CoreImMessageBean.class);
            message.setId(ApplicationContextHelper.getNextIdentity());
            message.setSendTime(LocalDateTime.now());
            List<CoreFileBean> fileList = this.getFileListByMessage(message);
            if (!fileList.isEmpty()) {
               fileList.forEach(e -> e.setTargetId(this.getDao().getTable() + "$" + message.getId()));
               this.fileService.getDao().update(fileList, "TARGETID");
            }

            Set<String> onlineUser = ImHelper.getOnlineUser();
            if (onlineUser.contains(message.getReceiveUserId())) {
               CoreImMessageBean message4Send = new CoreImMessageBean();
               BeanUtils.copyProperties(message, message4Send);
               message4Send.setOfflineFlag("1");
               if (!fileList.isEmpty()) {
                  message4Send.setContent(this.buildImMessageContentWithFile(message.getContent(), fileList));
               }

               CoreImMessageDTO messageDTO = this.buildMessage(message4Send);
               this.imWebSocket.sendToUser(messageDTO);
            }

            message.setOfflineFlag("1");
            this.getDao().insert(message);
            this.coreImContactsService.insertByMessage(message);
         });
      } catch (Exception var3) {
         logger.error(var3.getMessage(), (Throwable)var3);
         return false;
      }

      return true;
   }

   private String buildImMessageContentWithFile(String content, List<CoreFileBean> fileList) {
      Map<String, String> param = new HashMap<>();
      fileList.forEach(e -> {
         String fileId = String.valueOf(e.getId());
         String replacedStr = fileId + "__$$__" + e.getName() + "__$$__" + this.fileManager.getAbsoluteDownloadUrl(e);
         param.put(fileId, replacedStr);
      });
      String replacedContent = content;

      for (Entry<String, String> entry : param.entrySet()) {
         replacedContent = StringUtils.replace(replacedContent, entry.getKey(), entry.getValue());
      }

      return replacedContent;
   }

   private List<CoreFileBean> getFileListByMessage(List<CoreImMessageBean> messageList) {
      List<String> fileIdStrList = new ArrayList<>();
      messageList.forEach(e -> {
         String content = e.getContent();
         if (StringUtils.contains(content, "[!img=") || StringUtils.contains(content, "[!file=")) {
            fileIdStrList.addAll(StringUtils.substringsBetween(content, "[!img=", "]"));
            fileIdStrList.addAll(StringUtils.substringsBetween(content, "[!file=", "]"));
         }
      });
      if (fileIdStrList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<Long> fileIdList = fileIdStrList.stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
         return this.fileService.selectListByIds(fileIdList);
      }
   }

   private List<CoreFileBean> getFileListByMessage(CoreImMessageBean message) {
      String content = message.getContent();
      if (!StringUtils.contains(content, "[!img=") && !StringUtils.contains(content, "[!file=")) {
         return CollectionUtils.emptyList();
      } else {
         List<String> fileIdStrList = StringUtils.substringsBetween(content, "[!img=", "]");
         fileIdStrList.addAll(StringUtils.substringsBetween(content, "[!file=", "]"));
         if (fileIdStrList.isEmpty()) {
            return CollectionUtils.emptyList();
         } else {
            List<Long> fileIdList = fileIdStrList.stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
            return this.fileService.selectListByIds(fileIdList);
         }
      }
   }

   private CoreImMessageDTO buildMessage(CoreImMessageBean message) {
      CoreImMessageDTO messageDTO = new CoreImMessageDTO();
      messageDTO.setReceiveUserId(message.getReceiveUserId());
      messageDTO.setType("message");
      messageDTO.setData(JSONObject.toJSONString(message));
      return messageDTO;
   }
}
