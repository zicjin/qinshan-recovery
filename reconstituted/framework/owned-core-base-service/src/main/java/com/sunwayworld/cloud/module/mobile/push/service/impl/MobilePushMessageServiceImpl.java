package com.sunwayworld.cloud.module.mobile.push.service.impl;

import com.sunwayworld.cloud.module.mobile.helper.GetuiHelper;
import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushMessageBean;
import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.cloud.module.mobile.push.persistent.dao.MobilePushMessageDao;
import com.sunwayworld.cloud.module.mobile.push.persistent.dao.MobilePushUserDao;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushMessageService;
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
public class MobilePushMessageServiceImpl implements MobilePushMessageService {
   @Autowired
   private MobilePushMessageDao mobilePushMessageDao;
   @Autowired
   private MobilePushUserDao mobilePushUserDao;

   public MobilePushMessageDao getDao() {
      return this.mobilePushMessageDao;
   }

   public MobilePushUserDao getMobilePushUserDao() {
      return this.mobilePushUserDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      MobilePushMessageBean bean = jsonWrapper.parseUnique(MobilePushMessageBean.class);
      bean.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(bean);
      return bean.getId();
   }

   @Override
   public void send(RestJsonWrapperBean wrapper) {
      ArrayList<MobilePushMessageBean> itemList = (ArrayList<MobilePushMessageBean>)wrapper.<MobilePushMessageBean>parse(MobilePushMessageBean.class);
      itemList.forEach(bean -> {
         if (!StringUtils.isEmpty(bean.getTitle()) && !StringUtils.isEmpty(bean.getContent()) && !StringUtils.isEmpty(bean.getPushedUserId())) {
            String[] userIds = StringUtils.split(bean.getPushedUserId(), ",");

            for (String userId : userIds) {
               if (!StringUtils.isEmpty(userId)) {
                  MobilePushUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
                  if (mobilePushUserBean != null && !StringUtils.isEmpty(mobilePushUserBean.getClientId())) {
                     String message = "";

                     try {
                        message = GetuiHelper.sendMsg(mobilePushUserBean.getClientId(), bean.getTitle(), bean.getContent(), bean.getData());
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
   public void insertPushMessage(String userId, String title, String content, String data) {
      MobilePushMessageBean bean = new MobilePushMessageBean();
      bean.setId(ApplicationContextHelper.getNextIdentity());
      bean.setPushedUserId(userId);
      bean.setTitle(title);
      bean.setContent(content);
      bean.setData(data);
      if (StringUtils.isEmpty(bean.getTitle()) || StringUtils.isEmpty(bean.getContent()) || StringUtils.isEmpty(bean.getPushedUserId())) {
         this.setPushRemark(bean, "标题，内容或被推送人数据为空！");
      } else if (!StringUtils.isEmpty(userId)) {
         MobilePushUserBean mobilePushUserBean = this.getMobilePushUserDao().selectByIdIfPresent(userId);
         if (mobilePushUserBean != null && !StringUtils.isEmpty(mobilePushUserBean.getClientId())) {
            String message = "";

            try {
               message = GetuiHelper.sendMsg(mobilePushUserBean.getClientId(), bean.getTitle(), bean.getContent(), bean.getData());
            } catch (Exception var9) {
               var9.printStackTrace();
               message = var9.getMessage();
            }

            this.setPushRemark(bean, message);
            this.setPushProperties(bean, "ok".equals(message));
         } else {
            this.setPushRemark(bean, "该用户无设备ID信息！");
         }
      } else {
         this.setPushRemark(bean, "被推送人数据为空！");
      }

      this.getDao().insert(bean);
   }

   private void setPushProperties(MobilePushMessageBean proxy, boolean flag) {
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

   private void setPushRemark(MobilePushMessageBean proxy, String remark) {
      if (StringUtils.isEmpty(proxy.getRemark())) {
         proxy.setRemark(remark);
      } else {
         proxy.setRemark(proxy.getRemark() + "," + remark);
      }
   }
}
