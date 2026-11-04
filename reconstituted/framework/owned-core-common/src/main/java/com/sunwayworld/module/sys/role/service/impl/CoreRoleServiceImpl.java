package com.sunwayworld.module.sys.role.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.dao.CoreRoleDao;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreRoleServiceImpl implements CoreRoleService {
   @Autowired
   private CoreRoleDao roleDao;
   @Autowired
   private CoreRoleUserService roleUserService;
   @Autowired
   private CoreRolePermissionService permissionService;
   @Autowired
   private CoreDataCenterUserService coreDataCenterUserService;

   public CoreRoleDao getDao() {
      return this.roleDao;
   }

   @Cacheable(
      value = {"T_CORE_ROLE"},
      key = "'ALL'"
   )
   @Override
   public List<CoreRoleBean> selectAll() {
      return CoreRoleService.super.selectAll();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreRoleBean role = wrapper.parseUnique(CoreRoleBean.class);
      role.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(role);
      return role.getId();
   }

   @Override
   public Page<CoreRoleUserBean> selectRoleUserPaginationByRole(Long roleId, RestJsonWrapperBean wrapper) {
      return this.roleUserService.selectPaginationByRole(roleId, wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertRoleUser(Long id, RestJsonWrapperBean wrapper) {
      List<CoreRoleUserBean> roleUserList = wrapper.parse(CoreRoleUserBean.class);
      roleUserList.forEach(u -> {
         u.setRoleId(id);
         this.roleUserService.insertByUserId(u.getUserId(), Arrays.asList(id));
      });
      this.coreDataCenterUserService.syncAfterRoleUserModify(roleUserList, "roleAddUser");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteRoleUser(RestJsonWrapperBean wrapper) {
      List<Long> roleUserIdList = wrapper.parseId(Long.class);
      List<CoreRoleUserBean> roleUserList = this.roleUserService.selectListByIds(roleUserIdList);
      Map<String, List<Long>> mapList = roleUserList.stream()
         .collect(Collectors.groupingBy(CoreRoleUserBean::getUserId, Collectors.mapping(CoreRoleUserBean::getRoleId, Collectors.toList())));
      this.coreDataCenterUserService.syncAfterRoleUserModify(roleUserList, "roleUserDelete");
      mapList.forEach((k, v) -> this.roleUserService.deleteByUserId(k, (List<Long>)v));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertRolePermission(Long id, String permissionTypeId, RestJsonWrapperBean wrapper) {
      List<CoreRolePermissionBean> permissionList = wrapper.parse(CoreRolePermissionBean.class);
      this.permissionService.insert(id, permissionTypeId, permissionList.stream().map(CoreRolePermissionBean::getTargetId).collect(Collectors.toList()));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteRolePermission(Long id, String permissionTypeId, RestJsonWrapperBean wrapper) {
      List<CoreRolePermissionBean> permissionList = wrapper.parse(CoreRolePermissionBean.class);
      this.permissionService.delete(id, permissionTypeId, permissionList.stream().map(CoreRolePermissionBean::getTargetId).collect(Collectors.toList()));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<CoreRoleBean> roleList = wrapper.parse(CoreRoleBean.class);
      if (!roleList.isEmpty()) {
         roleList.forEach(r -> {
            this.roleUserService.deleteByRoleId(r.getId());
            this.permissionService.deleteByRoleId(r.getId());
         });
         CoreRoleService.super.delete(wrapper);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void doSuspendAction(RestJsonWrapperBean wrapper, boolean suspend) {
      List<CoreRoleBean> roleList = wrapper.parse(CoreRoleBean.class);
      if (!roleList.isEmpty()) {
         CoreRoleService.super.doSuspendAction(wrapper, suspend);
      }
   }

   @Override
   public Page<CoreRoleBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setSuspendedFlag("0");
      parameter.setOrgAuthority();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Override
   public boolean isPermissionTypeAdminRole(Long roleId, String permissionTypeId) {
      List<CoreRolePermissionBean> permissionList = this.permissionService.selectListByRole(roleId);
      return permissionList.stream().anyMatch(p -> permissionTypeId.equals(p.getPermissionTypeId()) && "0".equals(p.getTargetId()));
   }

   @Override
   public List<TableHeadBean> selectPermissionMatrixHeadList() {
      List<CoreRoleBean> roleList = this.getDao().selectAll();
      return roleList.stream().map(e -> {
         TableHeadBean head = new TableHeadBean();
         head.setTitle(e.getRoleName());
         head.setField(e.getId().toString());
         return head;
      }).collect(Collectors.toList());
   }

   @Override
   public RestValidationResultBean deleteValidate(RestJsonWrapperBean wrapper) {
      List<CoreRoleBean> roleList = wrapper.parse(CoreRoleBean.class);
      List<Long> roleIdList = roleList.stream().map(CoreRoleBean::getId).collect(Collectors.toList());
      List<CoreRoleUserBean> roleUserList = this.roleUserService.getDao().selectListByOneColumnValues(roleIdList, "ROLEID");
      List<CoreRolePermissionBean> rolePermissionList = this.permissionService.getDao().selectListByOneColumnValues(roleIdList, "ROLEID");
      boolean validateFlag = true;
      StringBuilder messageBuilder = new StringBuilder();

      for (CoreRoleBean role : roleList) {
         List<CoreRoleUserBean> subRoleUserList = roleUserList.stream().filter(f -> f.getRoleId().equals(role.getId())).collect(Collectors.toList());
         List<CoreRolePermissionBean> subRolePermissionList = rolePermissionList.stream()
            .filter(f -> f.getRoleId().equals(role.getId()))
            .collect(Collectors.toList());
         if (!subRoleUserList.isEmpty() || !subRolePermissionList.isEmpty()) {
            validateFlag = false;
            messageBuilder.append(role.getRoleName() + ":");
            if (!subRoleUserList.isEmpty() && subRolePermissionList.isEmpty()) {
               messageBuilder.append(I18nHelper.getMessage("MODULE.SYS.ROLES.TIP.EXISTS_ROLE_USERS"));
            } else if (subRoleUserList.isEmpty() && !subRolePermissionList.isEmpty()) {
               messageBuilder.append(I18nHelper.getMessage("MODULE.SYS.ROLES.TIP.EXISTS_ROLE_PERMISSIONS"));
            } else {
               messageBuilder.append(I18nHelper.getMessage("MODULE.SYS.ROLES.TIP.EXISTS_ROLE_USERS_AND_PERMISSIONS"));
            }

            messageBuilder.append(";");
         }
      }

      return new RestValidationResultBean(validateFlag, messageBuilder.toString());
   }
}
