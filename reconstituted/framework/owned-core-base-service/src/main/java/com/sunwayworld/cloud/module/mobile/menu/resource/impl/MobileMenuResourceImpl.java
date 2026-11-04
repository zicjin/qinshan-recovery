package com.sunwayworld.cloud.module.mobile.menu.resource.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuIcon;
import com.sunwayworld.cloud.module.mobile.menu.resource.MobileMenuResource;
import com.sunwayworld.cloud.module.mobile.menu.service.MobileMenuService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule("移动端菜单配置")
@RestController
@GikamBean
@RequestMapping({"/secure/core/module/sys/mobile/menus"})
public class MobileMenuResourceImpl
   implements MobileMenuResource,
   AbstractGenericResource<MobileMenuService, MobileMenuBean, Long>,
   AbstractGenericActivatableResource<MobileMenuService, MobileMenuBean, Long>,
   AbstractGenericChoosableResource<MobileMenuService, MobileMenuBean, Long> {
   @Autowired
   private MobileMenuService mobileMenuService;

   public MobileMenuService getService() {
      return this.mobileMenuService;
   }

   @Override
   public Pagination<MobileMenuBean> selectRawPagination(RestJsonWrapperBean wrapper) {
      Pagination<MobileMenuBean> menuBeanPage = (Pagination<MobileMenuBean>)this.getService().selectRawPagination(wrapper);
      menuBeanPage.getRows()
         .stream()
         .peek(i -> i.setExt$Item("menuIconUrl", StringUtils.isEmpty(i.getIcon()) ? null : "/static/icon/todo/" + i.getIcon()))
         .collect(Collectors.toList());
      return menuBeanPage;
   }

   @Log(
      value = "查询菜单图标",
      type = LogType.SELECT
   )
   @Override
   public ArrayList<MobileMenuIcon> selectIconList() {
      return this.getService().selectIconList();
   }
}
