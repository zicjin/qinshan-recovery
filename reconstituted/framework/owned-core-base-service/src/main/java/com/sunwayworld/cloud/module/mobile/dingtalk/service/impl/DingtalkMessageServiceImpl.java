package com.sunwayworld.cloud.module.mobile.dingtalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkMessageBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkUserBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.config.DingtalkConfiguration;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.DingtalkMessageDao;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.DingtalkUserDao;
import com.sunwayworld.cloud.module.mobile.dingtalk.service.DingtalkMessageService;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.NotificationMsgType;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.BasePushResult;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.DingtalkConfigStore;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.DingtalkMessageUtil;
import com.sunwayworld.cloud.module.mobile.helper.GetuiHelper;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class DingtalkMessageServiceImpl implements DingtalkMessageService {
   @Autowired
   private DingtalkMessageDao mobilePushMessageDao;
   @Autowired
   private DingtalkUserDao dingtalkUserDao;

   public DingtalkMessageDao getDao() {
      return this.mobilePushMessageDao;
   }

   public DingtalkUserDao getMobilePushUserDao() {
      return this.dingtalkUserDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      DingtalkMessageBean bean = jsonWrapper.parseUnique(DingtalkMessageBean.class);
      bean.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(bean);
      return bean.getId();
   }

   @Override
   public void send(RestJsonWrapperBean wrapper) {
      ArrayList<DingtalkMessageBean> itemList = (ArrayList<DingtalkMessageBean>)wrapper.<DingtalkMessageBean>parse(DingtalkMessageBean.class);
      itemList.forEach(bean -> {
         if (!StringUtils.isEmpty(bean.getTitle()) && !StringUtils.isEmpty(bean.getContent()) && !StringUtils.isEmpty(bean.getPushedUserId())) {
            String[] userIds = StringUtils.split(bean.getPushedUserId(), ",");

            for (String userId : userIds) {
               if (!StringUtils.isEmpty(userId)) {
                  DingtalkUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
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
      DingtalkMessageBean bean = new DingtalkMessageBean();
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
         DingtalkUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
         if (mobilePushUserBean != null && !StringUtils.isEmpty(mobilePushUserBean.getDtuserid())) {
            String message = "";

            try {
               List<String[]> descriptions = new ArrayList<>();
               JSONArray descArr = JSONArray.parseArray(content);

               for (int index = 0; index < descArr.size(); index++) {
                  JSONObject obj = descArr.getJSONObject(index);
                  descriptions.add(new String[]{obj.getString("title"), obj.getString("value")});
               }

               DingtalkConfigStore dingtalkService = DingtalkConfiguration.getDtService(agentid);
               String url = dingtalkService.getAppConfig().getAppMsgUrl();
               if (!StringUtils.isEmpty(data)) {
                  if (url.contains("?")) {
                     url = url + "&" + data;
                  } else {
                     url = url + "?" + data;
                  }
               }

               BasePushResult pushResult = DingtalkMessageUtil.sendWorkRecordTextCardMsg(
                  Integer.parseInt(agentid), title, descriptions, url, "", mobilePushUserBean.getDtuserid(), "", ""
               );
               if (pushResult.getState() == 1) {
                  message = "ok";
               } else {
                  message = pushResult.getErrorMsg();
               }

               errMsg = message;
            } catch (Exception var15) {
               var15.printStackTrace();
               message = var15.getMessage();
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
         BasePushResult pushResult = DingtalkMessageUtil.completeWorkRecordTextCardMsg(Integer.parseInt(agentId), userId, recordId);
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

   private void setPushProperties(DingtalkMessageBean proxy, boolean flag) {
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

   private void setPushRemark(DingtalkMessageBean proxy, String remark) {
      if (StringUtils.isEmpty(proxy.getRemark())) {
         proxy.setRemark(remark);
      } else {
         proxy.setRemark(proxy.getRemark() + "," + remark);
      }
   }

   @Override
   public String sendNotificationMsg(String agentid, String userId, int code, String msg) {
      String errMsg = "";
      JSONObject msgObj = JSON.parseObject(msg);
      NotificationMsgType notificationMsgType = NotificationMsgType.getNotificationMsgTypeByCode(code);
      if (!StringUtils.isEmpty(userId)) {
         DingtalkUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
         if (mobilePushUserBean != null && !StringUtils.isEmpty(mobilePushUserBean.getDtuserid())) {
            String message = "";

            try {
               BasePushResult pushResult = DingtalkMessageUtil.sendNotificationMsg(
                  Integer.parseInt(agentid), mobilePushUserBean.getDtuserid(), notificationMsgType, msgObj
               );
               if (pushResult.getState() == 1) {
                  message = "ok";
               } else {
                  message = pushResult.getErrorMsg();
               }

               errMsg = message;
            } catch (Exception var11) {
               var11.printStackTrace();
               message = var11.getMessage();
               errMsg = message;
            }

            if (!"ok".equals(message)) {
               errMsg = message;
            }
         } else {
            errMsg = "该用户无设备ID信息！";
         }
      } else {
         errMsg = "被推送人数据为空！";
      }

      return errMsg;
   }
}
