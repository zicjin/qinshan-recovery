package com.sunwayworld.module.sys.role.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.matrix.bean.MatrixHeaderBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterUserService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.dao.CoreRoleUserDao;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreRoleUserServiceImpl implements CoreRoleUserService {
   @Autowired
   private CoreRoleUserDao roleUserDao;
   @Lazy
   @Autowired
   private CoreRoleService coreRoleService;
   @Lazy
   @Autowired
   private CoreUserService coreUserService;
   @Lazy
   @Autowired
   private CoreRoleUserService proxyInstance;
   @Autowired
   private CoreDataCenterUserService coreDataCenterUserService;

   public CoreRoleUserDao getDao() {
      return this.roleUserDao;
   }

   @Cacheable(
      value = {"T_CORE_ROLE_USER"},
      key = "'ALL'"
   )
   @Override
   public List<CoreRoleUserBean> selectAll() {
      return CoreRoleUserService.super.selectAll();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Caching(
      evict = {@CacheEvict(
         value = {"T_CORE_ROLE_USER.BY_USERID"},
         key = "#userId"
      ), @CacheEvict(
         value = {"T_CORE_ROLE_USER"},
         key = "'ALL'"
      )}
   )
   @Override
   public Long insertByUserId(String userId, List<Long> roleIdList) {
      if (roleIdList.isEmpty()) {
         return null;
      } else {
         for (Long roleId : roleIdList) {
            if (roleId == null || this.coreRoleService.getDao().selectByIdIfPresent(roleId) == null) {
               throw new ApplicationWarningException("GIKAM.EXCEPTION.NO_VALUE_EXISTS");
            }
         }
         List<CoreRoleUserBean> insertRoleUserList = roleIdList.stream().map(r -> {
            CoreRoleUserBean roleUser = new CoreRoleUserBean();
            roleUser.setId(ApplicationContextHelper.getNextIdentity());
            roleUser.setRoleId(r);
            roleUser.setUserId(userId);
            return roleUser;
         }).collect(Collectors.toList());
         this.getDao().insert(insertRoleUserList);
         return insertRoleUserList.get(0).getId();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Caching(
      evict = {@CacheEvict(
         value = {"T_CORE_ROLE_USER.BY_USERID"},
         key = "#userId"
      ), @CacheEvict(
         value = {"T_CORE_ROLE_USER"},
         key = "'ALL'"
      )}
   )
   @Override
   public void deleteByUserId(String userId, List<Long> roleIdList) {
      if (!roleIdList.isEmpty()) {
         CoreRoleUserBean filter = new CoreRoleUserBean();
         filter.setUserId(userId);
         List<CoreRoleUserBean> roleUserList = this.getDao().selectList(filter, new Order[0]);
         this.getDao()
            .deleteByIdList(roleUserList.stream().filter(r -> roleIdList.contains(r.getRoleId())).map(CoreRoleUserBean::getId).collect(Collectors.toList()));
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Caching(
      evict = {@CacheEvict(
         value = {"T_CORE_ROLE_USER.BY_USERID"},
         key = "#userId"
      ), @CacheEvict(
         value = {"T_CORE_ROLE_USER"},
         key = "'ALL'"
      )}
   )
   @Override
   public void deleteByUserId(String userId) {
      CoreRoleUserBean filter = new CoreRoleUserBean();
      filter.setUserId(userId);
      this.getDao().deleteBy(filter, new String[]{"USERID"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteByRoleId(Long roleId) {
      CoreRoleUserBean filter = new CoreRoleUserBean();
      filter.setRoleId(roleId);
      List<CoreRoleUserBean> roleUserList = this.getDao().selectList(filter, new Order[0]);
      if (!roleUserList.isEmpty()) {
         roleUserList.forEach(r -> this.proxyInstance.deleteByUserId(r.getUserId(), Arrays.asList(r.getRoleId())));
      }
   }

   @Cacheable(
      value = {"T_CORE_ROLE_USER.BY_USERID"},
      key = "#userId"
   )
   @Override
   public List<CoreRoleUserBean> selectByUserId(String userId) {
      CoreRoleUserBean filter = new CoreRoleUserBean();
      filter.setUserId(userId);
      return this.getDao().selectListByOneColumnValue(userId, "USERID", new Order[0]);
   }

   @Override
   public List<String> selectUserList(Long roleId) {
      CoreRoleUserBean filter = new CoreRoleUserBean();
      filter.setRoleId(roleId);
      return this.getDao().selectColumnList(filter, "USERID", String.class, new Order[0]);
   }

   @Override
   public Page<CoreRoleUserBean> selectPaginationByRole(Long roleId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setFilter(SearchFilter.instance().match("ROLEID", roleId).filter(MatchPattern.EQ));
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insert(RestJsonWrapperBean wrapper) {
      List<CoreRoleUserBean> roleUserList = wrapper.parse(CoreRoleUserBean.class);
      Map<String, List<Long>> mapList = roleUserList.stream()
         .collect(Collectors.groupingBy(CoreRoleUserBean::getUserId, Collectors.mapping(CoreRoleUserBean::getRoleId, Collectors.toList())));
      mapList.forEach((k, v) -> this.proxyInstance.insertByUserId(k, (List<Long>)v));
      this.coreDataCenterUserService.syncAfterRoleUserModify(roleUserList, "userAddRole");
      return null;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Caching(
      evict = {@CacheEvict(
         value = {"T_CORE_ROLE_USER.BY_USERID"},
         key = "#userId"
      ), @CacheEvict(
         value = {"T_CORE_ROLE_USER"},
         key = "'ALL'"
      )}
   )
   @Override
   public void setDefault(String userId, RestJsonWrapperBean wrapper) {
      List<CoreRoleUserBean> updateCoreRoleUserList = new ArrayList<>();
      List<CoreRoleUserBean> coreRoleUserList = this.selectListByFilter(
         SearchFilter.instance().match("USERID", userId).filter(MatchPattern.EQ).match("DEFAULTFLAG", "1").filter(MatchPattern.EQ), new Order[0]
      );
      if (!coreRoleUserList.isEmpty()) {
         CoreRoleUserBean roleUser = coreRoleUserList.get(0);
         roleUser.setDefaultFlag("0");
         updateCoreRoleUserList.add(roleUser);
      }

      CoreRoleUserBean defaultRoleUser = wrapper.parseUnique(CoreRoleUserBean.class);
      defaultRoleUser.setDefaultFlag("1");
      updateCoreRoleUserList.add(defaultRoleUser);
      this.getDao().update(updateCoreRoleUserList, new String[]{"DEFAULTFLAG"});
   }

   @Override
   public List<MatrixHeaderBean> selectMatrixHeaderList() {
      List<CoreRoleBean> roleList = this.coreRoleService.getDao().selectAll(Order.asc("ORDERNO"));
      return roleList.stream().map(r -> {
         MatrixHeaderBean header = MatrixHeaderBean.getRotateInstance(this.coreRoleService.getDao().getTable(), r.getId(), "ROLENAME", r.getRoleName());
         header.setJavaType(String.class.getSimpleName());
         header.setType("simpleCheckbox");
         header.setEditor(true);
         header.setReadonly(false);
         return header;
      }).collect(Collectors.toList());
   }

   @Override
   public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      List<CoreUserBean> coreUserList = this.coreUserService.selectList(MatrixHelper.getStandardMapperParameter(parameter));
      if (coreUserList.isEmpty()) {
         return new Pagination<>();
      } else {
         List<String> userIdList = coreUserList.stream().map(CoreUserBean::getId).collect(Collectors.toList());
         List<CoreRoleUserBean> allRoleUserList = this.proxyInstance.selectAll();
         List<CoreRoleUserBean> roleUserList = allRoleUserList.stream().filter(e -> userIdList.contains(e.getUserId())).collect(Collectors.toList());
         Map<String, List<CoreRoleUserBean>> map = roleUserList.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getUserId, Collectors.toList()));
         List<MatrixRowMap> rowList = new ArrayList<>();

         for (CoreUserBean user : coreUserList) {
            List<CoreRoleUserBean> filteredRoleList = map.get(user.getId());
            MatrixRowMap row = new MatrixRowMap();
            row.putRowId(this.coreUserService.getDao().getTable(), user.getId());
            row.putItem(user);
            if (filteredRoleList != null && !filteredRoleList.isEmpty()) {
               filteredRoleList.forEach(ru -> row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1"));
               row.setPredicate(
                  t -> MatchPattern.EQ.name().equals(t.getMatchPattern())
                     ? t.getMap()
                        .entrySet()
                        .stream()
                        .allMatch(
                           e -> {
                              Object v = CollectionUtils.getValueIgnorecase(row, e.getKey());
                              return StringUtils.startsWith(e.getKey(), this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())
                                 ? v == null || "0".equals(v)
                                 : ObjectUtils.equals(v, e.getValue());
                           }
                        )
                     : t.match(row)
               );
            }

            rowList.add(row);
         }

         List<MatrixRowMap> filteredRowList = MatrixHelper.getFilteredRowList(parameter, rowList);
         MatrixHelper.sortRowList(parameter, filteredRowList);
         Page<MatrixRowMap> matrixRowPagination = new Pagination<>(filteredRowList);
         Map<String, String> userColumnTypeMap = new HashMap<>();
         userColumnTypeMap.put("id", "string");
         userColumnTypeMap.put("username", "string");
         matrixRowPagination.setColumnTypeMap(userColumnTypeMap);
         List<MatrixHeaderBean> headerList = this.selectMatrixHeaderList();
         headerList.forEach(m -> {
            String var10000 = matrixRowPagination.getColumnTypeMap().put(m.getField(), m.getJavaType().toLowerCase());
         });
         return matrixRowPagination;
      }
   }

   @Transactional
   @Override
   public void save(List<Map<String, Object>> mapList) {
      mapList.forEach(m -> {
         String userId = (String)CollectionUtils.getValueIgnorecase((Map<String, ?>)m, "id");
         String key = m.keySet().stream().filter(k -> k.startsWith("T_CORE_ROLE$ROLENAME$")).findAny().get();
         Long roleId = NumberUtils.parseLong(StringUtils.removeStart(key, "T_CORE_ROLE$ROLENAME$"));
         CoreRoleUserBean filter = new CoreRoleUserBean();
         filter.setUserId(userId);
         filter.setRoleId(roleId);
         CoreRoleUserBean roleUser = this.getDao().selectFirstIfPresent(filter, new Order[0]);
         if ("1".equals(m.get(key))) {
            if (roleUser == null) {
               this.proxyInstance.insertByUserId(userId, Arrays.asList(roleId));
            }
         } else if (roleUser != null) {
            this.proxyInstance.deleteByUserId(userId, Arrays.asList(roleId));
         }
      });
   }
}
