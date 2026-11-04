package com.sunwayworld.module.admin.menu.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.menu.bean.CoreAdminMenuBean;
import com.sunwayworld.module.admin.menu.bean.CoreAdminMenuDTO;
import com.sunwayworld.module.admin.menu.dao.CoreAdminMenuDao;
import com.sunwayworld.module.admin.menu.service.CoreAdminMenuService;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminMenuServiceImpl implements CoreAdminMenuService {
   @Autowired
   private CoreAdminMenuDao menuDao;
   @Autowired
   @Lazy
   private CoreAdminMenuService proxyInstance;
   @Autowired
   private CoreSystemConfigService systemConfigService;

   public CoreAdminMenuDao getDao() {
      return this.menuDao;
   }

   @Cacheable(
      value = {"T_CORE_ADMIN_MENU"},
      key = "'ALL'"
   )
   @Override
   public List<CoreAdminMenuBean> selectAll() {
      return CoreAdminMenuService.super.selectAll();
   }

   @Override
   public List<CoreAdminMenuDTO> selectTree(MapperParameter parameter) {
      boolean admin = LocalContextHelper.isAdminLogin();
      if (!admin) {
         return CollectionUtils.emptyList();
      } else {
         List<CoreAdminMenuBean> menuList = this.proxyInstance.selectAll();
         if (ApplicationContextHelper.getEnvironment().getProperty("sunway.design.studio.enabled", Boolean.class, Boolean.FALSE)) {
            menuList.removeIf(m -> StringUtils.startsWith(m.getId(), "1020"));
         }

         CoreSystemConfigDTO systemConfig = this.systemConfigService.selectSystemConfig();
         String urlPrefix = "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + systemConfig.getPageSkin() + "/";
         menuList.stream().filter(m -> !StringUtils.isEmpty(m.getMenuIcon())).forEach(m -> m.setExt$Item("menuIconUrl", urlPrefix + m.getMenuIcon()));
         TreeDescriptor<CoreAdminMenuBean> descriptor = new TreeDescriptor<>("id", "parentId", "menuName", "orderNo");
         descriptor.addConsumer((s, t) -> t.setHref(s.getHref()));
         return TreeHelper.parseTreeNode(menuList, descriptor, CoreAdminMenuDTO.class);
      }
   }
}
