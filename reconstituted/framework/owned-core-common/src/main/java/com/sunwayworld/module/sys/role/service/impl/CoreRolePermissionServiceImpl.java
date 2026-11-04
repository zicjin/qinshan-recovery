package com.sunwayworld.module.sys.role.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.MaskImplementationException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.dao.CoreRolePermissionDao;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreRolePermissionServiceImpl implements CoreRolePermissionService {
   @Autowired
   private CoreRolePermissionDao permissionDao;
   @Lazy
   @Autowired
   private CoreRoleUserService roleUserService;
   @Lazy
   @Autowired
   private CoreRoleService roleService;
   @Lazy
   @Autowired
   private CoreRolePermissionService proxyInstance;
   @Autowired
   private CoreLoginConfigService loginConfigService;
   @Lazy
   @Autowired
   private CoreMenuService menuService;

   public CoreRolePermissionDao getDao() {
      return this.permissionDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      List<CoreRolePermissionBean> rolePermissionList = wrapper.parse(CoreRolePermissionBean.class);
      if (!rolePermissionList.isEmpty()) {
         Map<Long, List<CoreRolePermissionBean>> map = rolePermissionList.stream().collect(Collectors.groupingBy(CoreRolePermissionBean::getRoleId));
         map.forEach(
            (r, m) -> {
               Map<String, List<String>> targetMap = m.stream()
                  .collect(
                     Collectors.toMap(
                        CoreRolePermissionBean::getPermissionTypeId, x -> new ArrayList<>(Collections.singletonList(x.getTargetId())), (o1List, o2List) -> {
                           o1List.addAll(o2List);
                           return o1List;
                        }
                     )
                  );
               targetMap.forEach((p, t) -> this.proxyInstance.insert(r, p, (List<String>)t));
            }
         );
      }

      return null;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insert(Long roleId, String permissionTypeId, List<String> targetIdList) {
      Set<String> permissionIdSet = this.proxyInstance
         .selectListByRole(roleId)
         .stream()
         .filter(p -> permissionTypeId.equals(p.getPermissionTypeId()))
         .map(CoreRolePermissionBean::getTargetId)
         .collect(Collectors.toSet());
      List<CoreRolePermissionBean> insertPermissionList = targetIdList.stream().filter(t -> !permissionIdSet.contains(t)).map(t -> {
         CoreRolePermissionBean permission = new CoreRolePermissionBean();
         permission.setId(ApplicationContextHelper.getNextIdentity());
         permission.setRoleId(roleId);
         permission.setPermissionTypeId(permissionTypeId);
         permission.setTargetId(t);
         return permission;
      }).collect(Collectors.toList());
      this.getDao().insert(insertPermissionList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<Long> idList = wrapper.parseId(Long.class);
      if (!idList.isEmpty()) {
         List<CoreRolePermissionBean> rolePermissionList = this.getDao().selectListByIds(idList, new Order[0]);
         Map<Long, List<CoreRolePermissionBean>> map = rolePermissionList.stream().collect(Collectors.groupingBy(CoreRolePermissionBean::getRoleId));
         map.forEach(
            (r, m) -> {
               Map<String, List<String>> targetMap = m.stream()
                  .collect(
                     Collectors.toMap(
                        CoreRolePermissionBean::getPermissionTypeId, x -> new ArrayList<>(Collections.singletonList(x.getTargetId())), (o1List, o2List) -> {
                           o1List.addAll(o2List);
                           return o1List;
                        }
                     )
                  );
               targetMap.forEach((p, t) -> this.proxyInstance.delete(r, p, (List<String>)t));
            }
         );
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(Long roleId, String permissionTypeId, List<String> targetIdList) {
      List<CoreRolePermissionBean> deletePermissionList = null;
      if ("T_CORE_MENU".equals(permissionTypeId)) {
         List<CoreMenuBean> allMenuList = this.menuService.selectAll();
         List<String> roleMenuIdList = this.proxyInstance
            .selectListByFilter(
               SearchFilter.instance().match("ROLEID", roleId).filter(MatchPattern.EQ).match("PERMISSIONTYPEID", "T_CORE_MENU").filter(MatchPattern.EQ),
               new Order[]{Order.asc("ID")}
            )
            .stream()
            .map(t -> t.getTargetId())
            .collect(Collectors.toList());
         List<String> remainMenuIdList = roleMenuIdList.stream().filter(c -> !targetIdList.contains(c)).collect(Collectors.toList());
         if (remainMenuIdList.size() > 0) {
            List<CoreMenuBean> remainMenuList = allMenuList.stream().filter(c -> remainMenuIdList.contains(c.getId())).collect(Collectors.toList());
            List<String> remainMenuParentIdList = remainMenuList.stream().map(e -> e.getParentId()).distinct().collect(Collectors.toList());
            List<String> deleteTargetIdList = targetIdList.stream().filter(c -> !remainMenuParentIdList.contains(c)).collect(Collectors.toList());
            deletePermissionList = deleteTargetIdList.stream().map(t -> {
               CoreRolePermissionBean permission = new CoreRolePermissionBean();
               permission.setRoleId(roleId);
               permission.setPermissionTypeId(permissionTypeId);
               permission.setTargetId(t);
               return permission;
            }).collect(Collectors.toList());
            this.getDao().deleteBy(deletePermissionList, new String[]{"ROLEID", "TARGETID", "PERMISSIONTYPEID"});
            return;
         }
      }

      deletePermissionList = targetIdList.stream().map(t -> {
         CoreRolePermissionBean permission = new CoreRolePermissionBean();
         permission.setRoleId(roleId);
         permission.setPermissionTypeId(permissionTypeId);
         permission.setTargetId(t);
         return permission;
      }).collect(Collectors.toList());
      this.getDao().deleteBy(deletePermissionList, new String[]{"ROLEID", "TARGETID", "PERMISSIONTYPEID"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteByRoleId(Long roleId) {
      CoreRolePermissionBean filter = new CoreRolePermissionBean();
      filter.setRoleId(roleId);
      this.getDao().deleteBy(filter, new String[]{"ROLEID"});
   }

   @Cacheable(
      value = {"T_CORE_ROLE_PERMISSION.BY_ROLEID"},
      key = "'' + #roleId"
   )
   @Override
   public List<CoreRolePermissionBean> selectListByRole(Long roleId) {
      CoreRolePermissionBean filter = new CoreRolePermissionBean();
      filter.setRoleId(roleId);
      return this.getDao().selectList(filter, new Order[0]);
   }

   @Override
   public List<CoreRolePermissionBean> selectActiveUserPermissionList(String userId, String permissionType) {
      List<CoreRoleUserBean> roleUserList = this.roleUserService.selectByUserId(userId);
      List<CoreRolePermissionBean> rolePermissionList = new ArrayList<>();

      for (CoreRoleUserBean roleUser : roleUserList) {
         CoreRoleBean role = this.roleService.selectByIdIfPresent(roleUser.getRoleId());
         if (role != null
            && "0".equals(role.getLastSuspendedFlag())
            && (
               !"T_CORE_MENU".equals(permissionType)
                  || !"1".equals(this.loginConfigService.selectLoginConfig().getLoginOrg())
                  || role.getUsedByOrgId() == null
                  || ArrayUtils.contains(StringUtils.split(role.getUsedByOrgId(), ","), LocalContextHelper.getLoginOrgId())
            )) {
            List<CoreRolePermissionBean> allPermissionList = this.proxyInstance.selectListByRole(role.getId());
            rolePermissionList.addAll(allPermissionList.stream().filter(p -> permissionType.equals(p.getPermissionTypeId())).collect(Collectors.toList()));
         }
      }

      return rolePermissionList;
   }

   @Override
   public String selectPermissionTypeAdmin(String permisionTypeId) {
      for (CoreRoleUserBean roleUser : this.roleUserService.selectByUserId(LocalContextHelper.getLoginUserId())) {
         CoreRoleBean role = this.roleService.selectByIdIfPresent(roleUser.getRoleId());
         if (role != null) {
            List<CoreRolePermissionBean> allPermissionList = this.proxyInstance.selectListByRole(role.getId());
            if (allPermissionList.stream().anyMatch(p -> p.getPermissionTypeId().equals(permisionTypeId) && "0".equals(p.getTargetId()))) {
               return "1";
            }
         }
      }

      return "0";
   }

   public void delete(Long id) {
      throw new MaskImplementationException();
   }

   public void update(CoreRolePermissionBean proxy) {
      throw new MaskImplementationException();
   }

   @Override
   public void updateIfChanged(List<CoreRolePermissionBean> itemList) {
      throw new MaskImplementationException();
   }

   public void updateIfChanged(CoreRolePermissionBean item) {
      throw new MaskImplementationException();
   }

   public void save(Long id, RestJsonWrapperBean wrapper) {
      throw new MaskImplementationException();
   }
}
