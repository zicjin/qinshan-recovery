package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserPermissionDao;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreUserPermissionServiceImpl implements CoreUserPermissionService {
   @Autowired
   private CoreUserPermissionDao permissionDao;
   @Autowired
   @Lazy
   private AuthenticationProvider authenticationProvider;
   @Autowired
   private CoreRoleUserService coreRoleUserService;

   public CoreUserPermissionDao getDao() {
      return this.permissionDao;
   }

   @Transactional
   public Long insert(RestJsonWrapperBean wrapper) {
      List<CoreUserPermissionBean> userPermissionList = wrapper.parse(CoreUserPermissionBean.class);
      if (userPermissionList.isEmpty()) {
         return null;
      } else {
         userPermissionList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
         this.getDao().insert(userPermissionList);
         return userPermissionList.get(0).getId();
      }
   }

   @Transactional
   @Override
   public void insert(String userId, String permissionTypeId, RestJsonWrapperBean wrapper) {
      List<CoreUserPermissionBean> userPermissionList = wrapper.parse(CoreUserPermissionBean.class);
      userPermissionList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setUserId(userId);
         e.setPermissionTypeId(permissionTypeId);
      });
      this.getDao().insert(userPermissionList);
   }

   @Override
   public Page<CoreUserPermissionBean> selectPaginationByPermissionType(String permissionTypeId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> {
         byte var4x = -1;
         switch (permissionTypeId.hashCode()) {
            case 1079996847:
               if (permissionTypeId.equals("T_CORE_ORG")) {
                  var4x = 0;
               }
            default:
               switch (var4x) {
                  case 0:
                     return this.getDao().selectOrgPermision(parameter);
                  default:
                     return this.getDao().selectByCondition(parameter);
               }
         }
      });
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, CollectionUtils.emptyList());
      } else {
         List<CoreUserPermissionBean> itemList = page.getRows()
            .stream()
            .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getDao().getType()))
            .collect(Collectors.toList());
         return new Pagination<>(page, itemList);
      }
   }

   @Override
   public List<CoreUserPermissionBean> selectLoginPermissionList(RestJsonWrapperBean wrapper, String permissionTypeId) {
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

      MapperParameter parameter = new MapperParameter();
      parameter.put("user_assigned", userId);
      switch (permissionTypeId) {
         case "T_CORE_ORG":
            return this.getDao()
               .selectOrgPermision(parameter)
               .stream()
               .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getDao().getType()))
               .collect(Collectors.toList());
         case "T_CORE_ROLE":
            return this.coreRoleUserService
               .selectListByFilter(SearchFilter.instance().match("USERID", userId).filter(MatchPattern.EQ), new Order[0])
               .stream()
               .map(e -> {
                  CoreUserPermissionBean userPermission = new CoreUserPermissionBean();
                  userPermission.setTargetId(e.getRoleId().toString());
                  userPermission.setExt$Item("rolename", e.getExt$().get("rolename"));
                  userPermission.setDefaultFlag(e.getDefaultFlag());
                  return userPermission;
               })
               .collect(Collectors.toList());
         default:
            return CollectionUtils.emptyList();
      }
   }
}
