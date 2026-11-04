package com.sunwayworld.module.admin.tenant.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantDTO;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantUserBean;
import com.sunwayworld.module.admin.tenant.dao.CoreAdminTenantDao;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantUserService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdminTenantServiceImpl implements CoreAdminTenantService {
   @Autowired
   private CoreAdminTenantDao tenantDao;
   @Lazy
   @Autowired
   private CoreAdminTenantUserService tenantUserService;
   @Lazy
   @Autowired
   private CoreUserService userService;
   @Autowired
   private TenantManager tenantManager;

   public CoreAdminTenantDao getDao() {
      return this.tenantDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public String insert(RestJsonWrapperBean wrapper) {
      CoreAdminTenantBean tenant = wrapper.parseUnique(CoreAdminTenantBean.class);
      this.getDao().insert(tenant);
      return tenant.getId();
   }

   @Cacheable(
      value = {"T_CORE_ADMIN_TENANT"},
      key = "'ALL'"
   )
   @Override
   public List<CoreAdminTenantBean> selectAll() {
      return CoreAdminTenantService.super.selectAll();
   }

   @Transactional
   @Override
   public void insertUsers(String id, RestJsonWrapperBean jsonWrapper) {
      List<CoreAdminTenantUserBean> tenantUserList = jsonWrapper.parse(CoreAdminTenantUserBean.class);
      tenantUserList.forEach(u -> {
         u.setId(ApplicationContextHelper.getNextIdentity());
         u.setTenantId(id);
      });
      this.tenantUserService.getDao().insert(tenantUserList);
      List<String> userIdList = tenantUserList.stream().map(CoreAdminTenantUserBean::getUserId).collect(Collectors.toList());
      List<CoreUserBean> userList = this.userService.getDao().selectListByIds(userIdList);
      this.tenantManager.tenantCall("INSERT_USER", id, () -> this.userService.tenantTriggerInsertOrActivateUser(userList));
   }

   @Transactional
   @Override
   public void deleteUsers(String id, RestJsonWrapperBean jsonWrapper) {
      List<Long> idList = jsonWrapper.parseId(Long.class);
      List<CoreAdminTenantUserBean> tenantUserList = this.tenantUserService.selectListByIds(idList);
      this.tenantUserService.getDao().deleteByIdList(idList);
      this.tenantManager
         .tenantCall(
            "INSERT_USER",
            id,
            () -> this.userService.tenantTriggerDeactivateUser(tenantUserList.stream().map(CoreAdminTenantUserBean::getUserId).collect(Collectors.toList()))
         );
   }

   @Override
   public Page<CoreAdminTenantUserBean> selectUserPagination(String id, RestJsonWrapperBean wrapper) {
      return this.tenantUserService.selectPaginationByFilter(SearchFilter.instance().match("TENANTID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public boolean isSynchronizing(String userId) {
      return RedisHelper.exists("TENANT_SYNC", userId);
   }

   @Transactional
   @Override
   public void syncUser(String id, CoreUserBean oldItem, CoreUserBean newItem) {
      String userId = Optional.ofNullable(oldItem).orElse(newItem).getId();

      try {
         RedisHelper.put("TENANT_SYNC", userId, "1");
         if (oldItem == null) {
            if (!TenantContext.isMaster(id)) {
               this.tenantManager.masterCall("MASTER_SYNC_USER_INSERT", () -> {
                  CoreUserBean existingUser = this.userService.selectByIdIfPresent(newItem.getId());
                  if (existingUser != null) {
                     this.tenantSyncUser(id, newItem);
                  } else {
                     this.userService.getDao().insert(newItem);
                     if ("activated".equals(newItem.getStatus()) && "0".equals(newItem.getLastSuspendedFlag())) {
                        CoreAdminTenantUserBean tenantUser = new CoreAdminTenantUserBean();
                        tenantUser.setId(ApplicationContextHelper.getNextIdentity());
                        tenantUser.setTenantId(id);
                        tenantUser.setUserId(newItem.getId());
                        this.tenantUserService.getDao().insert(tenantUser);
                     }
                  }
               });
            }
         } else if (newItem == null) {
            if (TenantContext.isMaster(id)) {
               this.tenantManager.call("SYNC_DELETE_USER", () -> {
                  CoreUserBean existingUser = this.userService.selectByIdIfPresent(oldItem.getId());
                  if (existingUser != null) {
                     this.userService.tenantTriggerDeactivateUser(Arrays.asList(existingUser.getId()));
                  }
               });
               this.tenantManager.masterCall("MASTER_SYNC_USER_DELETE", () -> {
                  CoreAdminTenantUserBean filter = new CoreAdminTenantUserBean();
                  filter.setUserId(oldItem.getId());
                  this.tenantUserService.getDao().deleteBy(filter, "TENANTID", "USERID");
               });
            } else {
               this.tenantManager.masterCall("MASTER_SYNC_USER_DELETE", () -> {
                  CoreAdminTenantUserBean filter = new CoreAdminTenantUserBean();
                  filter.setTenantId(id);
                  filter.setUserId(oldItem.getId());
                  this.tenantUserService.getDao().deleteBy(filter, "TENANTID", "USERID");
               });
            }
         } else {
            if (!"activated".equals(newItem.getStatus()) || !"0".equals(newItem.getLastSuspendedFlag())) {
               this.tenantManager.masterCall("MASTER_SYNC_USER_UPDATE", () -> {
                  CoreAdminTenantUserBean filter = new CoreAdminTenantUserBean();
                  filter.setTenantId(id);
                  filter.setUserId(oldItem.getId());
                  this.tenantUserService.getDao().deleteBy(filter, "TENANTID", "USERID");
               });
            } else if (!TenantContext.isMaster(id)) {
               this.tenantManager.masterCall("MASTER_SYNC_USER_UPDATE", () -> {
                  CoreAdminTenantUserBean tenantUser = new CoreAdminTenantUserBean();
                  tenantUser.setTenantId(id);
                  tenantUser.setUserId(newItem.getId());
                  List<CoreAdminTenantUserBean> tenanUserList = this.tenantUserService.selectList(tenantUser, new Order[0]);
                  if (tenanUserList.isEmpty()) {
                     tenantUser.setId(ApplicationContextHelper.getNextIdentity());
                     this.tenantUserService.getDao().insert(tenantUser);
                  }
               });
            }

            this.tenantSyncUser(id, newItem);
         }
      } finally {
         RedisHelper.evict("TENANT_SYNC", userId);
      }
   }

   @Override
   public List<CoreAdminTenantDTO> selectLoginTenantList(RestJsonWrapperBean wrapper) {
      if (!ApplicationContextHelper.isProfileActivated("tenant")) {
         return CollectionUtils.emptyList();
      } else {
         String userId = null;
         if (LocalContextHelper.isUserLogin()) {
            userId = LocalContextHelper.getLoginUserId();
         } else {
            SunwayAopContext.putCache("username", wrapper.getParamValue("username"));
            SunwayAopContext.putCache("password", AuthenticationHelper.getRawPassword(wrapper.getParamValue("password")));
            SunwayAopContext.putCache("mobile", wrapper.getParamValue("mobile"));
            SunwayAopContext.putCache("smscode", wrapper.getParamValue("smscode"));
            SsoAuthenticator authenticator = SsoAuthenticatorRegistry.INSTANCE.getAuthenticator("testLogin");
            if (authenticator == null) {
               return CollectionUtils.emptyList();
            }

            LoginUser loginUser = authenticator.authenticate();
            if (loginUser == null) {
               return CollectionUtils.emptyList();
            }

            userId = loginUser.getUsername();
         }

         CoreAdminTenantUserBean filter = new CoreAdminTenantUserBean();
         filter.setUserId(userId);
         List<CoreAdminTenantUserBean> tenantUserList = this.tenantUserService.selectList(filter, new Order[0]);
         if (tenantUserList.isEmpty()) {
            return CollectionUtils.emptyList();
         } else {
            List<CoreAdminTenantBean> tenantList = this.getDao()
               .selectListByIds(tenantUserList.stream().map(CoreAdminTenantUserBean::getTenantId).collect(Collectors.toList()), new Order[0]);
            return tenantList.isEmpty() ? CollectionUtils.emptyList() : tenantList.stream().map(t -> {
               CoreAdminTenantDTO tenant = new CoreAdminTenantDTO();
               tenant.setId(t.getId());
               tenant.setTenantName(t.getTenantName());
               return tenant;
            }).collect(Collectors.toList());
         }
      }
   }

   private void tenantSyncUser(String tenantId, CoreUserBean user) {
      this.tenantManager
         .call(
            "SYNC_UPDATE_USER",
            () -> {
               CoreUserBean existingUser = this.userService.selectByIdIfPresent(user.getId());
               if (existingUser != null
                  && (
                     !existingUser.getUserName().equals(user.getUserName())
                        || !existingUser.getPassword().equals(user.getPassword())
                        || !existingUser.getStatus().equals(user.getStatus())
                  )) {
                  existingUser.setUserName(user.getUserName());
                  existingUser.setPassword(user.getPassword());
                  existingUser.setStatus(user.getStatus());
                  this.userService.getDao().update(existingUser, "USERNAME", "PASSWORD", "STATUS");
               }
            },
            tenantId
         );
   }
}
