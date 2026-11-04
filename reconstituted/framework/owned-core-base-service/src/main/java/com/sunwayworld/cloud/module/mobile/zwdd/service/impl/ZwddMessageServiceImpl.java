package com.sunwayworld.cloud.module.mobile.zwdd.service.impl;

import com.sunwayworld.cloud.module.mobile.helper.GetuiHelper;
import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddMessageBean;
import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddUserBean;
import com.sunwayworld.cloud.module.mobile.zwdd.config.ZwddConfiguration;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.ZwddMessageDao;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.ZwddUserDao;
import com.sunwayworld.cloud.module.mobile.zwdd.service.ZwddMessageService;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.BasePushResult;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.ZwddConfigStore;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.ZwddMessageUtil;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class ZwddMessageServiceImpl implements ZwddMessageService {
   @Autowired
   private ZwddMessageDao mobilePushMessageDao;
   @Autowired
   private ZwddUserDao dingtalkUserDao;

   public ZwddMessageDao getDao() {
      return this.mobilePushMessageDao;
   }

   public ZwddUserDao getMobilePushUserDao() {
      return this.dingtalkUserDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      ZwddMessageBean bean = jsonWrapper.parseUnique(ZwddMessageBean.class);
      bean.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(bean);
      return bean.getId();
   }

   @Override
   public void send(RestJsonWrapperBean wrapper) {
      ArrayList<ZwddMessageBean> itemList = (ArrayList<ZwddMessageBean>)wrapper.<ZwddMessageBean>parse(ZwddMessageBean.class);
      itemList.forEach(bean -> {
         if (!StringUtils.isEmpty(bean.getTitle()) && !StringUtils.isEmpty(bean.getContent()) && !StringUtils.isEmpty(bean.getPushedUserId())) {
            String[] userIds = StringUtils.split(bean.getPushedUserId(), ",");

            for (String userId : userIds) {
               if (!StringUtils.isEmpty(userId)) {
                  ZwddUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
                  if (mobilePushUserBean != null && !StringUtils.isEmpty(mobilePushUserBean.getDtuserid())) {
                     String message = "";

                     try {
                        message = GetuiHelper.sendMsg(mobilePushUserBean.getDtuserid(), bean.getTitle(), bean.getContent(), bean.getData());
                     } catch (Exception var10) {
                        var10.printStackTrace();
                        message = var10.getMessage();
                     }

                     this.setPushRemark(bean, message);
                     this.setPushProperties(bean, "ok".equals(message));
                  } else {
                     this.setPushRemark(bean, "该用户无设备ID信息！");
                  }
               } else {
                  this.setPushRemark(bean, "被推送人数据为空！");
               }
            }
         } else {
            this.setPushRemark(bean, "标题，内容或被推送人数据为空！");
         }
      });
      this.getDao().update(itemList, new String[0]);
   }

   @Override
   public String insertPushMessage(String agentid, String userId, String title, String content, String data) {
      String errMsg = "";
      ZwddMessageBean bean = new ZwddMessageBean();
      bean.setId(ApplicationContextHelper.getNextIdentity());
      bean.setPushedUserId(userId);
      bean.setAgentid(agentid);
      bean.setTitle(title);
      bean.setContent(content);
      bean.setData(data);
      if (StringUtils.isEmpty(bean.getTitle()) || StringUtils.isEmpty(bean.getContent()) || StringUtils.isEmpty(bean.getPushedUserId())) {
         errMsg = "标题，内容或被推送人数据为空！";
         this.setPushRemark(bean, "标题，内容或被推送人数据为空！");
      } else if (!StringUtils.isEmpty(userId)) {
         ZwddUserBean zwddUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
         if (zwddUserBean != null && !StringUtils.isEmpty(zwddUserBean.getDtuserid())) {
            String message = "";

            try {
               ZwddConfigStore zwddService = ZwddConfiguration.getDtService(agentid);
               String url = zwddService.getAppConfig().getAppMsgUrl();
               if (!StringUtils.isEmpty(data)) {
                  if (url.contains("?")) {
                     url = url + "&" + data;
                  } else {
                     url = url + "?" + data;
                  }
               }

               BasePushResult pushResult = ZwddMessageUtil.sendLinkWorkRecordTextCardMsg(
                  agentid, title, content, url, zwddUserBean.getAccountid(), zwddUserBean.getTenantid()
               );
               if (pushResult.getState() == 1) {
                  message = "ok";
               } else {
                  message = pushResult.getErrorMsg();
               }

               errMsg = message;
            } catch (Exception var13) {
               var13.printStackTrace();
               message = var13.getMessage();
               errMsg = message;
            }

            this.setPushRemark(bean, message);
            if ("ok".equals(message)) {
               this.setPushProperties(bean, true);
            } else {
               errMsg = message;
               this.setPushProperties(bean, false);
            }
         } else {
            errMsg = "该用户无设备ID信息！";
            this.setPushRemark(bean, "该用户无设备ID信息！");
         }
      } else {
         errMsg = "被推送人数据为空！";
         this.setPushRemark(bean, "被推送人数据为空！");
      }

      this.getDao().insert(bean);
      return errMsg;
   }

   @Override
   public String clearPushMessage(String agentId, String userId, String recordId) {
      String errMsg = "";

      try {
         BasePushResult pushResult = ZwddMessageUtil.completeWorkRecordTextCardMsg(agentId, userId, recordId);
         if (pushResult.getState() == 1) {
            errMsg = "ok";
         } else {
            errMsg = pushResult.getErrorMsg();
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         errMsg = var6.getMessage();
      }

      return errMsg;
   }

   private void setPushProperties(ZwddMessageBean proxy, boolean flag) {
      proxy.setPushFlag(flag ? "1" : "0");
      if (flag) {
         proxy.setPushById(LocalContextHelper.getLoginUserId());
         proxy.setPushByName(LocalContextHelper.getLoginUser().getUserName());
         proxy.setPushTime(LocalDateTime.now());
      } else {
         proxy.setPushById(null);
         proxy.setPushByName(null);
         proxy.setPushTime(null);
      }
   }

   private void setPushRemark(ZwddMessageBean proxy, String remark) {
      if (StringUtils.isEmpty(proxy.getRemark())) {
         proxy.setRemark(remark);
      } else {
         proxy.setRemark(proxy.getRemark() + "," + remark);
      }
   }
}
