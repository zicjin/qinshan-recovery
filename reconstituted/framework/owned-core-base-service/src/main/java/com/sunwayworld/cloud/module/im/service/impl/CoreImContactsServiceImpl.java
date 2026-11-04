package com.sunwayworld.cloud.module.im.service.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsBean;
import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImContactsDao;
import com.sunwayworld.cloud.module.im.service.CoreImContactsService;
import com.sunwayworld.cloud.module.im.service.CoreImMessageService;
import com.sunwayworld.cloud.module.im.support.ImHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreImContactsServiceImpl implements CoreImContactsService {
   @Autowired
   private CoreImContactsDao coreImContactsDao;
   @Autowired
   @Lazy
   private CoreImMessageService coreImMessageService;
   @Autowired
   private CoreUserService coreUserService;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;
   private static final String IMG_OPEN_TAG = "[!img=";
   private static final String FILE_OPEN_TAG = "[!file=";
   private static final String IMG_FILE_CLOSE_TAG = "]";
   private static final String FILE_PROP_SEPARATOR = "__$$__";

   public CoreImContactsDao getDao() {
      return this.coreImContactsDao;
   }

   @Transactional
   @Override
   public void insertByMessage(CoreImMessageBean message) {
      List<CoreImContactsBean> insertContactsList = new ArrayList<>();
      CoreImContactsBean imContacts1 = new CoreImContactsBean();
      imContacts1.setId(ApplicationContextHelper.getNextIdentity());
      imContacts1.setUserId(message.getSendUserId());
      imContacts1.setContactsId(message.getReceiveUserId());
      List<CoreImContactsBean> imContactsList = this.selectListByFilter(
         SearchFilter.instance()
            .match("USERID", message.getSendUserId())
            .filter(MatchPattern.EQ)
            .match("CONTACTSID", message.getReceiveUserId())
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      CoreImContactsBean coreImContactsBean = imContactsList.stream().findAny().orElse(null);
      if (coreImContactsBean == null) {
         imContacts1.setTop("0");
      } else {
         imContacts1.setTop(coreImContactsBean.getTop());
      }

      imContacts1.setDeleted("0");
      insertContactsList.add(imContacts1);
      CoreImContactsBean imContacts2 = new CoreImContactsBean();
      List<CoreImContactsBean> imContactsReceiverList = this.selectListByFilter(
         SearchFilter.instance()
            .match("USERID", message.getReceiveUserId())
            .filter(MatchPattern.EQ)
            .match("CONTACTSID", message.getSendUserId())
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      CoreImContactsBean coreImContactsReceiverBean = imContactsReceiverList.stream().findAny().orElse(null);
      imContacts2.setId(ApplicationContextHelper.getNextIdentity());
      imContacts2.setUserId(message.getReceiveUserId());
      imContacts2.setContactsId(message.getSendUserId());
      if (coreImContactsReceiverBean == null) {
         imContacts2.setTop("0");
      } else {
         imContacts2.setTop(coreImContactsReceiverBean.getTop());
      }

      insertContactsList.add(imContacts2);
      this.getDao().deleteBy(insertContactsList, new String[]{"USERID", "CONTACTSID"});
      this.getDao().insert(insertContactsList);
   }

   @Transactional
   @Override
   public void topMessage(String userId) {
      String loginUserId = LocalContextHelper.getLoginUserId();
      List<CoreImContactsBean> imContactsList = this.selectListByFilter(
         SearchFilter.instance().match("USERID", loginUserId).filter(MatchPattern.EQ).match("CONTACTSID", userId).filter(MatchPattern.EQ), new Order[0]
      );
      if (!CollectionUtils.isEmpty(imContactsList)) {
         CoreImContactsBean updateBean = imContactsList.stream().findAny().orElse(null);
         updateBean.setTop("0".equals(updateBean.getTop()) ? "1" : "0");
         this.getDao().update(updateBean, new String[0]);
      }
   }

   @Transactional
   @Override
   public void deleteContacts(String userId) {
      String loginUserId = LocalContextHelper.getLoginUserId();
      List<CoreImContactsBean> imContactsList = this.selectListByFilter(
         SearchFilter.instance().match("USERID", loginUserId).filter(MatchPattern.EQ).match("CONTACTSID", userId).filter(MatchPattern.EQ), new Order[0]
      );
      if (!CollectionUtils.isEmpty(imContactsList)) {
         CoreImContactsBean updateBean = imContactsList.stream().findAny().orElse(null);
         updateBean.setDeleted("1");
         this.getDao().update(updateBean, new String[0]);
      }
   }

   @Override
   public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean wrapper) {
      String loginUserId = LocalContextHelper.getLoginUserId();
      List<CoreImContactsBean> imContactsList = this.selectListByFilter(
         SearchFilter.instance().match("USERID", loginUserId).filter(MatchPattern.EQ).match("DELETED", "0").filter(MatchPattern.EQ), wrapper
      );
      if (imContactsList.isEmpty()) {
         return new Pagination<>(CollectionUtils.emptyList());
      } else {
         List<String> contactsIdList = imContactsList.stream().map(e -> e.getContactsId()).collect(Collectors.toList());
         List<CoreUserBean> contactsList = this.coreUserService.selectListByIds(contactsIdList);
         List<CoreImMessageBean> receiveMessageList = this.coreImMessageService
            .selectListByFilter(
               SearchFilter.instance().match("RECEIVEUSERID", loginUserId).filter(MatchPattern.EQ).match("SENDUSERID", contactsIdList).filter(MatchPattern.OR),
               new Order[0]
            );
         List<CoreImMessageBean> sendMessageList = this.coreImMessageService
            .selectListByFilter(
               SearchFilter.instance().match("SENDUSERID", loginUserId).filter(MatchPattern.EQ).match("RECEIVEUSERID", contactsIdList).filter(MatchPattern.OR),
               new Order[0]
            );
         List<CoreImMessageBean> messageList = new ArrayList<>();
         messageList.addAll(receiveMessageList);
         messageList.addAll(sendMessageList);
         Map<String, String> avatarMap = this.coreUserService.selectAbsoluteAvatarUrl(contactsIdList);
         Set<String> onlineUserIdSet = ImHelper.getOnlineUser();
         List<CoreImContactsDTO> imContactsDTOList = contactsList.stream()
            .map(
               e -> {
                  CoreImContactsDTO imContactsDTO = new CoreImContactsDTO();
                  CoreImContactsBean imContactsBean = imContactsList.stream().filter(f -> f.getContactsId().equals(e.getId())).findAny().orElse(null);
                  imContactsDTO.setId(e.getId());
                  imContactsDTO.setTop(imContactsBean.getTop());
                  imContactsDTO.setUserId(e.getId());
                  imContactsDTO.setUserName(e.getUserName());
                  imContactsDTO.setEmail(e.getEmail());
                  imContactsDTO.setOrgName(e.getOrgName());
                  imContactsDTO.setAvatarUrl(avatarMap.get(e.getId()));
                  imContactsDTO.setSignature(avatarMap.get(e.getId()));
                  imContactsDTO.setStatus(onlineUserIdSet.contains(e.getId()) ? "online" : "offline");
                  List<CoreImMessageBean> subMessageList = messageList.stream()
                     .filter(f -> e.getId().equals(f.getSendUserId()) || e.getId().equals(f.getReceiveUserId()))
                     .collect(Collectors.toList());
                  CoreImMessageBean lastestMessage = subMessageList.stream().sorted((c, d) -> d.getId().compareTo(c.getId())).findFirst().get();
                  List<CoreFileBean> fileList = this.getFileListByMessage(lastestMessage);
                  lastestMessage.setContent(this.buildImMessageContentWithFile(lastestMessage.getContent(), fileList));
                  imContactsDTO.setLastestMessage(lastestMessage);
                  long offlineMessageQty = subMessageList.stream()
                     .filter(f -> "1".equals(f.getOfflineFlag()) && !loginUserId.equals(f.getSendUserId()))
                     .count();
                  imContactsDTO.setOfflineMessageQty(offlineMessageQty);
                  imContactsDTO.setLastestMessageTime(lastestMessage.getCreatedTime());
                  return imContactsDTO;
               }
            )
            .collect(Collectors.toList());
         List<CoreImContactsDTO> contactsDTOListList = imContactsDTOList.stream()
            .sorted(Comparator.comparing(CoreImContactsDTO::getLastestMessageTime).reversed())
            .sorted(Comparator.comparing(CoreImContactsDTO::getTop).reversed())
            .collect(Collectors.toList());
         return new Pagination<>(contactsDTOListList);
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
}
