package com.sunwayworld.module.item.tenant.service.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import com.sunwayworld.module.item.tenant.dao.CoreTenantPermissionDao;
import com.sunwayworld.module.item.tenant.service.CoreTenantPermissionService;
import com.sunwayworld.module.item.tenant.service.CoreTenantService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTenantPermissionServiceImpl implements CoreTenantPermissionService {
   @Autowired
   @Lazy
   private CoreTenantService coreTenantService;
   @Autowired
   private CoreTenantPermissionDao coreTenantPermissionDao;

   public CoreTenantPermissionDao getDao() {
      return this.coreTenantPermissionDao;
   }

   @Override
   public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean wrapper) {
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

      List<CoreTenantPermissionBean> tenantPermissionList = this.getDao().selectListByOneColumnValue(userId, "USERID", new Order[0]);
      List<Long> tenantIdList = tenantPermissionList.stream().map(e -> e.getTenantId()).collect(Collectors.toList());
      List<CoreTenantBean> tenantList = this.coreTenantService.selectListByIds(tenantIdList);
      Map<Long, CoreTenantBean> idTenantMap = tenantList.stream().collect(Collectors.toMap(CoreTenantBean::getId, t -> (CoreTenantBean)t));
      tenantPermissionList.forEach(e -> {
         CoreTenantBean tenant = idTenantMap.get(e.getTenantId());
         e.setExt$Item("tenantname", tenant.getTenantName());
         e.setExt$Item("tenanturl", tenant.getTenantUrl());
      });
      return tenantPermissionList;
   }
}
