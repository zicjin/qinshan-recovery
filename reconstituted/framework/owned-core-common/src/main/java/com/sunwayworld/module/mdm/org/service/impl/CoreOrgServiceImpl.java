package com.sunwayworld.module.mdm.org.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgDTO;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import com.sunwayworld.module.mdm.org.dao.CoreOrgDao;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.org.service.CoreOrgUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreOrgServiceImpl implements CoreOrgService {
   @Autowired
   private CoreOrgDao orgDao;
   @Autowired
   @Lazy
   private CoreOrgUserService orgUserService;
   @Autowired
   @Lazy
   private CoreOrgService proxyInstance;

   public CoreOrgDao getDao() {
      return this.orgDao;
   }

   @Cacheable(
      value = {"T_CORE_ORG"},
      key = "'ALL'"
   )
   @Override
   public List<CoreOrgBean> selectAll() {
      return this.getDao().selectAll();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public String insert(RestJsonWrapperBean wrapper) {
      CoreOrgBean coreOrg = wrapper.parseUnique(CoreOrgBean.class);
      coreOrg.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      this.getDao().insert(coreOrg);
      return coreOrg.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertUser(String id, RestJsonWrapperBean wrapper) {
      List<CoreOrgUserBean> orgUserList = wrapper.parse(CoreOrgUserBean.class);
      List<Long> idList = ApplicationContextHelper.getNextIdentityList(orgUserList.size());

      for (int index = 0; index < idList.size(); index++) {
         orgUserList.get(index).setId(idList.get(index));
      }

      this.orgUserService.getDao().insert(orgUserList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteUser(String id, RestJsonWrapperBean wrapper) {
      this.orgUserService.delete(wrapper);
   }

   @Override
   public List<CoreOrgDTO> selectTree(MapperParameter parameter) {
      List<CoreOrgBean> orgList = this.getDao().selectTreeNodeList(parameter);
      TreeDescriptor<CoreOrgBean> descriptor = new TreeDescriptor<>("id", "parentId", "orgName", "orderNo");
      return TreeHelper.parseTreeNode(orgList, descriptor, CoreOrgDTO.class);
   }

   @Override
   public Page<CoreOrgBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setOrgAuthority();
      parameter.setChoosableQueries();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(parameter, rowBounds);
   }

   @Override
   public Page<CoreOrgBean> selectRoleOrgList(Long roleId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.put("roleId", roleId);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> mapPage = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectRoleOrgList(parameter));
      List<CoreOrgBean> itemList = mapPage.getRows()
         .stream()
         .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreOrgBean.class))
         .collect(Collectors.toList());
      return new Pagination<>(mapPage, itemList);
   }

   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertOrgUser(String orgId, RestJsonWrapperBean wrapper) {
      CoreOrgBean org = this.getDao().selectById(orgId);
      List<CoreOrgUserBean> orgUserList = wrapper.parse(CoreOrgUserBean.class);
      List<Long> idList = ApplicationContextHelper.getNextIdentityList(orgUserList.size());

      for (int i = 0; i < idList.size(); i++) {
         CoreOrgUserBean orgUser = orgUserList.get(i);
         orgUser.setId(idList.get(i));
         orgUser.setOrgId(orgId);
         orgUser.setOrgName(org.getOrgName());
      }

      this.orgUserService.getDao().insert(orgUserList);
   }

   @Override
   public List<CoreOrgBean> selectChildOrgList(String orgId) {
      List<CoreOrgBean> allOrgList = this.proxyInstance.selectAll();
      CoreOrgBean org = allOrgList.stream().filter(o -> o.getId().equals(orgId)).findFirst().orElse(null);
      return org == null ? CollectionUtils.emptyList() : this.selectChildOrgList(allOrgList, org);
   }

   @Override
   public List<CoreOrgBean> selectParentOrgList(String orgId) {
      List<CoreOrgBean> allOrgList = this.proxyInstance.selectAll();
      CoreOrgBean org = allOrgList.stream().filter(o -> o.getId().equals(orgId)).findFirst().orElse(null);
      return org == null ? CollectionUtils.emptyList() : this.selectParentOrgList(allOrgList, org);
   }

   private List<CoreOrgBean> selectChildOrgList(List<CoreOrgBean> allList, CoreOrgBean currOrg) {
      List<CoreOrgBean> childOrgList = new ArrayList<>();

      for (CoreOrgBean org : allList) {
         if (ObjectUtils.equals(org.getParentId(), currOrg.getId())) {
            childOrgList.add(org);
            childOrgList.addAll(this.selectChildOrgList(allList, org));
         }
      }

      return childOrgList;
   }

   private List<CoreOrgBean> selectParentOrgList(List<CoreOrgBean> allList, CoreOrgBean currOrg) {
      List<CoreOrgBean> parentOrgList = new ArrayList<>();

      for (CoreOrgBean org : allList) {
         if (ObjectUtils.equals(org.getId(), currOrg.getParentId())) {
            parentOrgList.add(org);
            parentOrgList.addAll(this.selectParentOrgList(allList, org));
         }
      }

      return parentOrgList;
   }
}
