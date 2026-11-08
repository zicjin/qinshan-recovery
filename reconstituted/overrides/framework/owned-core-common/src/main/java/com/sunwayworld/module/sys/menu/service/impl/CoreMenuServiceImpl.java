package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterBean;
import com.sunwayworld.module.admin.helpcenter.service.CoreHelpCenterService;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyService;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;
import com.sunwayworld.module.sys.config.helper.SystemConfigHelper;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDeletedBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuIconDTO;
import com.sunwayworld.module.sys.menu.dao.CoreMenuDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuDeletedService;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreMenuServiceImpl implements CoreMenuService {
   @Autowired
   private CoreMenuDao menuDao;
   @Autowired
   @Lazy
   private CoreRolePermissionService permissionService;
   @Autowired
   @Lazy
   private CoreRoleUserService roleUserService;
   @Lazy
   @Autowired
   private CoreMenuService proxyInstance;
   @Lazy
   @Autowired
   private CoreAttorneyService attorneyService;
   @Autowired
   @Lazy
   private CoreMenuDeletedService coreMenuDeletedService;
   @Autowired
   @Lazy
   private CoreSystemConfigService systemConfigService;
   @Autowired
   @Lazy
   private CoreHelpCenterService coreHelpCenterService;
   private static final ThreadLocal<CoreMenuServiceImpl.SelectMenuRequiredData> LOCAL_SELECT_MENU_REQUIRED_DATA = new ThreadLocal<>();

   public CoreMenuDao getDao() {
      return this.menuDao;
   }

   @Cacheable(
      value = {"T_CORE_MENU"},
      key = "'ALL'"
   )
   @Override
   public List<CoreMenuBean> selectAll() {
      return CoreMenuService.super.selectAll();
   }

   @Override
   public List<CoreMenuBean> selectCurrentUserMenuList() {
      List<CoreRolePermissionBean> permissionList = this.permissionService.selectActiveUserPermissionList(LocalContextHelper.getLoginUserId(), "T_CORE_MENU");
      if (LocalContextHelper.getLoginRoleId() != null) {
         permissionList.removeIf(p -> !p.getRoleId().equals(LocalContextHelper.getLoginRoleId()));
      }

      boolean admin = permissionList.stream().anyMatch(p -> "0".equals(p.getTargetId()));
      List<CoreMenuBean> menuList = null;
      if (admin) {
         menuList = this.proxyInstance.selectAll();
      } else {
         Set<String> permittedMenuIdSet = permissionList.stream().map(p -> p.getTargetId()).distinct().collect(Collectors.toSet());
         if (permittedMenuIdSet.isEmpty()) {
            return CollectionUtils.emptyList();
         }

         menuList = this.proxyInstance.selectAll().stream().filter(m -> permittedMenuIdSet.contains(m.getId())).collect(Collectors.toList());
      }

      if (LocalContextHelper.isTrusteeLogin()) {
         List<String> entrustedMenuIdList = this.attorneyService.selectEntrustedMenuIdList();
         menuList.removeIf(m -> !entrustedMenuIdList.contains(m.getId()));
      }

      return menuList;
   }

   @Override
   public List<CoreMenuDTO> selectTree(MapperParameter parameter) {
      List<CoreMenuBean> menuList = this.selectCurrentUserMenuList();
      CoreSystemConfigBean systemConfig = this.systemConfigService.selectById("system-config");
      boolean helpCenter = StringUtils.equals(systemConfig.getHelpCenter(), "1");

      List var7;
      try {
         this.initSelectMenuRequiredData(systemConfig);
         TreeDescriptor<CoreMenuBean> descriptor = new TreeDescriptor<>("id", "parentId", "menuName", "orderNo");
         if (helpCenter) {
            List<CoreHelpCenterBean> helpCenterList = this.coreHelpCenterService.selectAll();
            this.getMenuHelpCenter(menuList, helpCenterList);
            descriptor.setParseTreeNodeFunction(t -> {
               CoreMenuDTO menu = CoreMenuDTO.of(t);
               menu.setExt$Item("menuIconUrl", this.getMenuIconUrl(t.getIcon()));
               menu.setHref(this.getReplacedHref(t));
               menu.setExt$Item("manualVideo", t.getExt$Item("manualvideo"));
               menu.setExt$Item("manualDoc", t.getExt$Item("manualdoc"));
               return menu;
            });
         } else {
            descriptor.setParseTreeNodeFunction(t -> {
               CoreMenuDTO menu = CoreMenuDTO.of(t);
               menu.setExt$Item("menuIconUrl", this.getMenuIconUrl(t.getIcon()));
               menu.setHref(this.getReplacedHref(t));
               return menu;
            });
         }

         List<CoreMenuDTO> treeMenuList = TreeHelper.parseTreeNode(menuList, descriptor, CoreMenuDTO.class);
         TreeHelper.updateChildQty(treeMenuList);
         var7 = treeMenuList;
      } finally {
         this.removeSelectMenuRequiredData();
      }

      return var7;
   }

   @Override
   public List<CoreMenuDTO> selectEditTree(String parentId, RestJsonWrapperBean wrapper) {
      List<CoreMenuBean> menuList = this.proxyInstance.selectAll();
      String resourceIdStr = wrapper.getParamValue("resourceId");
      if (!StringUtils.isEmpty(resourceIdStr)) {
         Long resourceId = Long.valueOf(resourceIdStr);
         List<CoreMenuBean> pulishMenuList = menuList.stream()
            .filter(menu -> menu.getResourceId() != null && menu.getResourceId() == resourceId)
            .collect(Collectors.toList());
         pulishMenuList.forEach(menu -> menu.setExt$Item("publishStatus", "1"));
         List<String> parentIdList = pulishMenuList.stream().map(CoreMenuBean::getParentId).collect(Collectors.toList());
         if (!parentIdList.isEmpty()) {
            this.generateMenuPublishStatus(menuList, parentIdList);
         }
      }

      List var8;
      try {
         CoreSystemConfigBean systemConfig = this.systemConfigService.selectById("system-config");
         this.initSelectMenuRequiredData(systemConfig);
         TreeDescriptor<CoreMenuBean> descriptor = new TreeDescriptor<>("id", "parentId", "menuName", "orderNo");
         descriptor.setParseTreeNodeFunction(t -> {
            CoreMenuDTO menu = CoreMenuDTO.of(t);
            menu.setPublishStatus(t.getExt$Item("publishStatus"));
            menu.setExt$Item("menuIconUrl", this.getMenuIconUrl(t.getIcon()));
            menu.setHref(this.getReplacedHref(t));
            return menu;
         });
         List<CoreMenuDTO> coreMenuDTOList = TreeHelper.parseTreeNode(menuList, descriptor, CoreMenuDTO.class);
         TreeHelper.updateChildQty(coreMenuDTOList);
         var8 = coreMenuDTOList;
      } finally {
         this.removeSelectMenuRequiredData();
      }

      return var8;
   }

   @Transactional
   public String insert(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      CoreMenuBean coreMenu = wrapper.parseUnique(CoreMenuBean.class);
      String id = ApplicationContextHelper.getNextSequence(this.getDao().getTable());
      Long orderNo = 1L;
      if (!StringUtils.isEmpty(coreMenu.getParentId())) {
         parameter.put("parentId", coreMenu.getParentId());
      } else {
         parameter.put("parentId", "root");
      }

      List<CoreMenuBean> menuList = this.getDao()
         .selectTreeNodeList(parameter)
         .stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, CoreMenuBean.class))
         .collect(Collectors.toList());
      if (!menuList.isEmpty()) {
         orderNo = menuList.stream().mapToLong(e -> NumberUtils.parseLong(e.getOrderNo())).max().getAsLong() + 1L;
      }

      coreMenu.setId(id);
      coreMenu.setMenuInitName(coreMenu.getMenuName());
      coreMenu.setOrderNo(orderNo);
      coreMenu.setPublishTime(LocalDateTime.now());
      coreMenu.setPublishUserId(LocalContextHelper.getLoginUserId());
      coreMenu.setPublishUserName(LocalContextHelper.getLoginUserName());
      this.getDao().insert(coreMenu);
      return coreMenu.getId();
   }

   @Transactional
   @Override
   public void swapOrderNo(String firstId, Long firstOrderNo, String secondId, Long secondOrderNo, RestJsonWrapperBean wrapper) {
      List<CoreMenuBean> menuList = new ArrayList<>();
      CoreMenuBean firstMenu = this.getDao().selectById(firstId);
      firstMenu.setOrderNo(secondOrderNo);
      menuList.add(firstMenu);
      CoreMenuBean secondMenu = this.getDao().selectById(secondId);
      secondMenu.setOrderNo(firstOrderNo);
      menuList.add(secondMenu);
      this.getDao().updateIfChanged(menuList);
   }

   @Override
   public Page<CoreMenuDTO> selectTreeList(RestJsonWrapperBean wrapper, Boolean... isStandardParameter) {
      String locale = LocaleContextHolder.getLocale().toString();
      String enMenuName = null;
      MapperParameter parameter = isStandardParameter.length > 0 && !isStandardParameter[0]
         ? MatrixHelper.getStandardMapperParameter(wrapper.extractMapFilter())
         : wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      if (!StringUtils.startsWith(locale, "zh")
         && parameter.get("filterParamPatterns") != null
         && parameter.get("filterParamPatterns") instanceof FilterParamPattern[]) {
         FilterParamPattern[] filterParamPatterns = (FilterParamPattern[])parameter.get("filterParamPatterns");
         List<FilterParamPattern> filterParamPatternList = Arrays.stream(filterParamPatterns)
            .filter(v -> CollectionUtils.containsIgnoreCase(v.getMap(), "MENUNAME"))
            .collect(Collectors.toList());
         List<FilterParamPattern> filterParamPatternListNotContains = Arrays.stream(filterParamPatterns)
            .filter(v -> !CollectionUtils.containsIgnoreCase(v.getMap(), "MENUNAME"))
            .collect(Collectors.toList());
         if (filterParamPatternList.size() > 0) {
            enMenuName = (String)CollectionUtils.getFirstValue(filterParamPatternList.get(0).getMap());
            parameter.remove("filterParamPatterns");
         }

         if (filterParamPatternListNotContains.size() > 0) {
            parameter.put("filterParamPatterns", filterParamPatternListNotContains.toArray());
         }
      }

      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectByCondition(parameter));
      List<CoreMenuBean> targetMenuList = page.getRows()
         .stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, CoreMenuBean.class))
         .collect(Collectors.toList());
      if (enMenuName != null) {
         targetMenuList.forEach(m -> {
            m.setExt$Item("menuIconUrl", this.getMenuIconUrl(m.getIcon()));
            m.setMenuName(!StringUtils.isBlank(m.getI18nCode()) ? I18nHelper.getMessage(m.getI18nCode()) : m.getMenuName());
            m.setMenuInitName(m.getMenuName());
         });
         String finalEnMenuName = enMenuName;
         targetMenuList = targetMenuList.stream().filter(v -> v.getMenuName().toUpperCase().indexOf(finalEnMenuName) > -1).collect(Collectors.toList());
      }

      Map<String, CoreMenuBean> allTreeMap = this.proxyInstance.selectAll().stream().collect(Collectors.toMap(CoreMenuBean::getId, Function.identity()));
      List<CoreMenuBean> treeList = new ArrayList<>();
      treeList.addAll(targetMenuList);
      Set<String> treeIdSet = treeList.stream().map(CoreMenuBean::getId).distinct().collect(Collectors.toSet());
      targetMenuList.forEach(u -> {
         String currMenuParentId = u.getParentId();
         if (!StringUtils.isEmpty(currMenuParentId)) {
            while (!treeIdSet.contains(currMenuParentId)) {
               CoreMenuBean currMenu = allTreeMap.get(currMenuParentId);
               if (null == currMenu) {
                  break;
               }

               treeIdSet.add(currMenu.getId());
               treeList.add(currMenu);
               currMenuParentId = currMenu.getParentId();
               if (StringUtils.isEmpty(currMenuParentId)) {
                  break;
               }
            }
         }
      });
      CoreSystemConfigBean systemConfig = this.systemConfigService.selectById("system-config");
      this.initSelectMenuRequiredData(systemConfig);
      treeList.forEach(m -> {
         m.setExt$Item("menuIconUrl", this.getMenuIconUrl(m.getIcon()));
         m.setMenuName(!StringUtils.isBlank(m.getI18nCode()) ? I18nHelper.getMessage(m.getI18nCode()) : m.getMenuName());
         m.setMenuInitName(m.getMenuName());
      });
      TreeDescriptor<CoreMenuBean> descriptor = new TreeDescriptor<>("id", "parentId", "menuName", "orderNo");
      descriptor.addConsumer((s, t) -> t.setHref(s.getHref()));
      List<CoreMenuDTO> treeNodeList = TreeHelper.parseTreeNode(treeList, descriptor, CoreMenuDTO.class);
      return new Pagination<>(page, treeNodeList);
   }

   @Transactional
   @Override
   public void drag(RestJsonWrapperBean wrapper) {
      String currentMenuId = wrapper.getParamValue("currentMenuId");
      String dropParentMenuId = wrapper.getParamValue("dropParentMenuId");
      String dropPreviousMenuId = wrapper.getParamValue("dropPreviousMenuId");
      CoreMenuBean currentMenu = this.getDao().selectById(currentMenuId);
      currentMenu.setParentId(dropParentMenuId);
      List<CoreMenuBean> updateMenuOrderNoList = new ArrayList<>();
      List<CoreMenuBean> coreMenuList = this.proxyInstance
         .selectListByFilter(SearchFilter.instance().match("PARENTID", dropParentMenuId).filter(MatchPattern.EQ), new Order[0]);
      if (ObjectUtils.isEmpty(dropPreviousMenuId)) {
         currentMenu.setOrderNo(1L);
      } else {
         CoreMenuBean previousMenu = this.getDao().selectById(dropPreviousMenuId);
         currentMenu.setOrderNo(previousMenu.getOrderNo() + 1L);
      }

      coreMenuList.stream().forEach(e -> {
         Long compareOrderNo = e.getOrderNo();
         if (e.getOrderNo() >= currentMenu.getOrderNo()) {
            e.setOrderNo(compareOrderNo + 1L);
            updateMenuOrderNoList.add(e);
         }
      });
      this.getDao().update(updateMenuOrderNoList, new String[]{"ORDERNO"});
      this.getDao().update(currentMenu, new String[]{"PARENTID", "ORDERNO"});
   }

   public CoreMenuBean selectDetail(String id) {
      CoreMenuBean menu = CoreMenuService.super.selectDetail(id);
      if (!StringUtils.isEmpty(menu.getTodoIcon())) {
         menu.setExt$Item("todoIconUrl", "/static/icon/todo/" + menu.getTodoIcon());
      }

      if (!StringUtils.isEmpty(menu.getIcon())) {
         menu.setExt$Item("menuIconUrl", this.getMenuIconUrl(menu.getIcon()));
      }

      return menu;
   }

   @Override
   public List<CoreMenuIconDTO> selectIconList() {
      CoreSystemConfigDTO systemConfig = SystemConfigHelper.getSystemConfig();
      String iconPath = "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + systemConfig.getPageSkin();
      if (systemConfig.getPageSkin().contains("#")) {
         iconPath = "/static/icon/menu/" + systemConfig.getPageStyle();
      }

      List<Resource> iconResourceList = ClassUtils.getResourceList(iconPath);
      return iconResourceList.stream().map(i -> {
         CoreMenuIconDTO icon = new CoreMenuIconDTO();
         icon.setIconName(i.getFilename());
         icon.setIconUrl(this.getMenuIconUrl(i.getFilename()));
         return icon;
      }).collect(Collectors.toList());
   }

   @Override
   public List<CoreMenuIconDTO> selectTodoIconList() {
      List<Resource> iconResourceList = ClassUtils.getResourceList("/static/icon/todo/");
      return iconResourceList.stream().map(i -> {
         CoreMenuIconDTO icon = new CoreMenuIconDTO();
         icon.setIconName(i.getFilename());
         icon.setIconUrl("/static/icon/todo/" + i.getFilename());
         return icon;
      }).collect(Collectors.toList());
   }

   @Override
   public CoreMenuBean selectPublishedMenuDetail(String id) {
      Map<String, Object> map = this.getDao().selectDetail(id);
      if (map != null && !map.isEmpty()) {
         CoreMenuBean menuBean = PersistableHelper.mapToPersistable(map, this.getDao().getType());
         List<CoreRolePermissionBean> coreRolePermissionList = this.permissionService
            .selectListByFilter(
               SearchFilter.instance().match("permissionTypeId", "T_CORE_MENU").filter(MatchPattern.EQ).match("targetId", id).filter(MatchPattern.EQ),
               new Order[0]
            );
         menuBean.setExt$Item("rolePermission", coreRolePermissionList.stream().map(CoreRolePermissionBean::getRoleId).collect(Collectors.toList()).toString());
         return menuBean;
      } else {
         return null;
      }
   }

   @Transactional
   @CacheEvict(
      value = {"T_CORE_MENU"},
      key = "'ALL'"
   )
   @Override
   public void savePublishedMenu(String id, RestJsonWrapperBean wrapper) {
      CoreMenuBean coreMenu = wrapper.parseUnique(CoreMenuBean.class);
      this.getDao().updateIfChanged(coreMenu);
      List<CoreRolePermissionBean> coreRolePermissionList = this.permissionService
         .selectListByFilter(
            SearchFilter.instance()
               .match("permissionTypeId", "T_CORE_MENU")
               .filter(MatchPattern.EQ)
               .match("targetId", coreMenu.getId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      this.permissionService.getDao().deleteBy(coreRolePermissionList);
      List<CoreRolePermissionBean> rolePermissionList = wrapper.parse(CoreRolePermissionBean.class);
      if (!rolePermissionList.isEmpty()) {
         rolePermissionList.forEach(rolePermission -> {
            rolePermission.setId(ApplicationContextHelper.getNextIdentity());
            rolePermission.setPermissionTypeId("T_CORE_MENU");
            rolePermission.setTargetId(coreMenu.getId());
         });
         this.permissionService.getDao().insert(rolePermissionList);
      }
   }

   @Override
   public String getReplacedHref(CoreMenuBean menu) {
      String href = menu.getHref();
      if (StringUtils.isEmpty(href)) {
         return href;
      } else {
         CoreMenuServiceImpl.SelectMenuRequiredData data = LOCAL_SELECT_MENU_REQUIRED_DATA.get();
         if (StringUtils.contains(href, "${loginUserId}")) {
            href = StringUtils.replace(href, "${loginUserId}", data == null ? LocalContextHelper.getLoginUserId() : data.getLoginUserId());
         }

         if (StringUtils.contains(href, "${loginOrgId}")) {
            href = StringUtils.replace(href, "${loginOrgId}", data == null ? LocalContextHelper.getLoginOrgId() : data.getLoginOrgId());
         }

         if (StringUtils.contains(href, "${loginRoleId}")) {
            href = StringUtils.replace(href, "${loginRoleId}", data == null ? LocalContextHelper.getLoginRoleId() : data.getLoginRoleId());
         }

         return href;
      }
   }

   @Transactional
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      CoreMenuBean menu = wrapper.parseUnique(CoreMenuBean.class);
      CoreMenuBean deleteMenu = this.selectById(menu.getId());
      CoreMenuDeletedBean md = new CoreMenuDeletedBean();
      BeanUtils.copyProperties(deleteMenu, md, "id");
      md.setId(ApplicationContextHelper.getNextIdentity());
      md.setMenuId(deleteMenu.getId());
      this.coreMenuDeletedService.getDao().insert(md);
      CoreMenuService.super.delete(wrapper);
   }

   private String getMenuIconUrl(String menuIcon) {
      if (StringUtils.isBlank(menuIcon) || "null".equalsIgnoreCase(menuIcon.trim()) || "undefined".equalsIgnoreCase(menuIcon.trim())) {
         return null;
      } else {
         CoreMenuServiceImpl.SelectMenuRequiredData data = LOCAL_SELECT_MENU_REQUIRED_DATA.get();
         if (data != null) {
            return data.getMenuIconUrlPrefix() + menuIcon;
         } else {
            CoreSystemConfigBean systemConfig = this.systemConfigService.selectById("system-config");
            String iconPath = "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + systemConfig.getPageSkin() + "/";
            if (systemConfig.getPageSkin().contains("#")) {
               iconPath = "/static/icon/menu/" + systemConfig.getPageStyle() + "/";
            }

            return iconPath + menuIcon;
         }
      }
   }

   private void getMenuHelpCenter(List<CoreMenuBean> menuList, List<CoreHelpCenterBean> helpCenterList) {
      menuList.forEach(
         e -> {
            CoreHelpCenterBean helpCenter = helpCenterList.stream().filter(f -> f.getId().equals(e.getId())).findFirst().orElse(null);
            if (helpCenter != null) {
               if (helpCenter.getManualVideoFileId() != null) {
                  e.setExt$Item("manualVideo", "1");
               } else {
                  e.setExt$Item("manualVideo", "0");
               }

               if ((!"file".equals(helpCenter.getManualEditingMethod()) || helpCenter.getManualDocFileId() == null)
                  && (!"online".equals(helpCenter.getManualEditingMethod()) || StringUtils.isBlank(helpCenter.getManualContent()))) {
                  e.setExt$Item("manualDoc", "0");
               } else {
                  e.setExt$Item("manualDoc", "1");
               }
            } else {
               e.setExt$Item("manualVideo", "0");
               e.setExt$Item("manualDoc", "0");
            }
         }
      );
   }

   private void initSelectMenuRequiredData(CoreSystemConfigBean systemConfig) {
      String menuIconUrlPrefix = "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + systemConfig.getPageSkin() + "/";
      if (systemConfig.getPageSkin().contains("#")) {
         menuIconUrlPrefix = "/static/icon/menu/" + systemConfig.getPageStyle() + "/";
      }

      LOCAL_SELECT_MENU_REQUIRED_DATA.set(
         CoreMenuServiceImpl.SelectMenuRequiredData.of(
            LocalContextHelper.getLoginUserId(), LocalContextHelper.getLoginOrgId(), LocalContextHelper.getLoginRoleId(), menuIconUrlPrefix
         )
      );
   }

   private void removeSelectMenuRequiredData() {
      LOCAL_SELECT_MENU_REQUIRED_DATA.remove();
   }

   private void generateMenuPublishStatus(List<CoreMenuBean> menuList, List<String> menuIdList) {
      List<CoreMenuBean> publishMenuList = menuList.stream().filter(menu -> menuIdList.contains(menu.getId())).collect(Collectors.toList());
      publishMenuList.forEach(menu -> menu.setExt$Item("publishStatus", "1"));
      List<String> parentIdList = publishMenuList.stream().map(CoreMenuBean::getParentId).collect(Collectors.toList());
      if (!parentIdList.isEmpty()) {
         this.generateMenuPublishStatus(menuList, parentIdList);
      }
   }

   private static class SelectMenuRequiredData {
      private String loginUserId;
      private String loginOrgId;
      private Long loginRoleId;
      private String menuIconUrlPrefix;

      public static CoreMenuServiceImpl.SelectMenuRequiredData of(String loginUserId, String loginOrgId, Long loginRoleId, String menuIconUrlPrefix) {
         CoreMenuServiceImpl.SelectMenuRequiredData instance = new CoreMenuServiceImpl.SelectMenuRequiredData();
         instance.loginUserId = loginUserId;
         instance.loginOrgId = loginOrgId;
         instance.loginRoleId = loginRoleId;
         instance.menuIconUrlPrefix = menuIconUrlPrefix;
         return instance;
      }

      public String getLoginUserId() {
         return this.loginUserId;
      }

      public String getLoginOrgId() {
         return this.loginOrgId;
      }

      public Long getLoginRoleId() {
         return this.loginRoleId;
      }

      public String getMenuIconUrlPrefix() {
         return this.menuIconUrlPrefix;
      }
   }
}
