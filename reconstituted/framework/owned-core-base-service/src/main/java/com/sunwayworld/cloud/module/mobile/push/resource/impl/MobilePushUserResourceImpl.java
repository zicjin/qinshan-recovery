package com.sunwayworld.cloud.module.mobile.push.resource.impl;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.cloud.module.mobile.push.resource.MobilePushUserResource;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushUserService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule("移动端推送用户绑定")
@RestController
@GikamBean
@RequestMapping({"/secure/core/module/sys/mobile/push_users"})
public class MobilePushUserResourceImpl
   implements MobilePushUserResource,
   AbstractGenericResource<MobilePushUserService, MobilePushUserBean, String>,
   AbstractGenericChoosableResource<MobilePushUserService, MobilePushUserBean, String> {
   @Autowired
   private MobilePushUserService mobilePushUserService;
   @Autowired
   private CoreUserService coreUserService;

   public MobilePushUserService getService() {
      return this.mobilePushUserService;
   }

   public CoreUserService getCoreUserService() {
      return this.coreUserService;
   }

   @Override
   public Pagination<MobilePushUserBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      Pagination<MobilePushUserBean> pushUserBeanPagination = (Pagination<MobilePushUserBean>)this.getService().selectRawPagination(wrapper);
      pushUserBeanPagination.getRows().stream().peek(mobilePushUserBean -> {
         CoreUserBean coreUserBean = this.getCoreUserService().selectByIdIfPresent(mobilePushUserBean.getId());
         if (coreUserBean != null) {
            mobilePushUserBean.setExt$Item("username", coreUserBean.getUserName());
         }
      }).collect(Collectors.toList());
      return pushUserBeanPagination;
   }

   @Override
   public void insertPushUser(String userId, String clientId) {
      this.getService().insertPushUser(userId, clientId);
   }
}
