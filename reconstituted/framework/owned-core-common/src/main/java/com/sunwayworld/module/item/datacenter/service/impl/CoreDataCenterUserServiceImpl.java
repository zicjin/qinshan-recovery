package com.sunwayworld.module.item.datacenter.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleUserDTO;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterUserDao;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterRoleService;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterService;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDataCenterUserServiceImpl implements CoreDataCenterUserService {
   @Autowired
   private CoreDataCenterUserDao coreDateCenterUserDao;
   @Autowired
   @Lazy
   private CoreDataCenterService coreDataCenterService;
   @Autowired
   @Lazy
   private CoreDataCenterRoleService coreDataCenterRoleService;
   @Autowired
   @Lazy
   private CoreRoleUserService roleUserService;

   public CoreDataCenterUserDao getDao() {
      return this.coreDateCenterUserDao;
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void syncAfterDataCenterRoleModify(List<CoreDataCenterRoleBean> dataCenterRoleList, String dataCenterOperationType) {
      if (!dataCenterRoleList.isEmpty()) {
         Long dataCenterId = dataCenterRoleList.get(0).getDataCenterId();
         List<String> roleIdList = dataCenterRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
         if ("dataCenterAddRole".equals(dataCenterOperationType)) {
            this.insertByDataCenterRole(dataCenterId, roleIdList);
         } else {
            CoreDataCenterUserBean deleteDataCenterUser = new CoreDataCenterUserBean();
            deleteDataCenterUser.setDataCenterId(dataCenterId);
            this.getDao().deleteBy(deleteDataCenterUser, new String[]{"DATACENTERID"});
            List<CoreDataCenterRoleBean> currentDataCenterRoleList = this.coreDataCenterRoleService
               .getDao()
               .selectListByOneColumnValue(dataCenterId, "DATACENTERID");
            if (!currentDataCenterRoleList.isEmpty()) {
               List<String> currentDataCenterRoleIdList = currentDataCenterRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
               this.insertByDataCenterRole(dataCenterId, currentDataCenterRoleIdList);
            }
         }
      }
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void syncAfterRoleUserModify(List<CoreRoleUserBean> roleUserList, String roleUserOperationType) {
      if (!roleUserList.isEmpty()) {
         List<Long> roleIdList = roleUserList.stream().map(e -> e.getRoleId()).distinct().collect(Collectors.toList());
         List<CoreDataCenterRoleBean> dataCenterRoleList = this.coreDataCenterRoleService.getDao().selectListByOneColumnValues(roleIdList, "ROLEID");
         if (!dataCenterRoleList.isEmpty()) {
            if ("roleAddUser".equals(roleUserOperationType)) {
               this.doAfterRoleAddUser(dataCenterRoleList, roleUserList);
            } else if ("roleUserDelete".equals(roleUserOperationType)) {
               if (roleIdList.size() > 1) {
                  this.doAfterUserDeleteRole(dataCenterRoleList, roleUserList);
               } else {
                  this.doAfterRoleDeleteUser(dataCenterRoleList, roleUserList, roleIdList);
               }
            } else if ("userAddRole".equals(roleUserOperationType)) {
               this.doAfterUserAddRole(dataCenterRoleList, roleUserList);
            }
         }
      }
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void sync(RestJsonWrapperBean wrapper) {
      this.getDao().deleteByIdList(this.selectAll().stream().map(e -> e.getId()).collect(Collectors.toList()));
      List<CoreDataCenterRoleUserDTO> dataCenterRoleUserDTOList = this.coreDataCenterRoleService.selectDataCenterRoleUserListByFilter(SearchFilter.instance());
      List<CoreDataCenterRoleUserDTO> distinctDataCenterRoleUserDTOList = dataCenterRoleUserDTOList.stream()
         .collect(
            Collectors.collectingAndThen(
               Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getDataCenterId() + ";" + o.getUserId()))), ArrayList::new
            )
         );
      List<CoreDataCenterUserBean> insertDataCenterUserList = distinctDataCenterRoleUserDTOList.stream().map(e -> {
         CoreDataCenterUserBean dataCenterUser = new CoreDataCenterUserBean();
         dataCenterUser.setId(ApplicationContextHelper.getNextIdentity());
         dataCenterUser.setDataCenterId(e.getDataCenterId());
         dataCenterUser.setUserId(e.getUserId());
         dataCenterUser.setActivatedFlag("1");
         return dataCenterUser;
      }).collect(Collectors.toList());
      if (!insertDataCenterUserList.isEmpty()) {
         this.getDao().insert(insertDataCenterUserList);
      }
   }

   private void insertByDataCenterRole(Long dataCenterId, List<String> roleIdList) {
      List<CoreRoleUserBean> roleUserList = this.roleUserService.getDao().selectListByOneColumnValues(roleIdList, "ROLEID");
      List<String> userIdList = roleUserList.stream().map(e -> e.getUserId()).distinct().collect(Collectors.toList());
      List<CoreDataCenterUserBean> dataCenterUserList = this.selectListByFilter(
         SearchFilter.instance().match("DATACENTERID", dataCenterId).filter(MatchPattern.EQ).match("USERID", userIdList).filter(MatchPattern.OR), new Order[0]
      );
      List<String> insertUserIdList = userIdList.stream()
         .filter(e -> dataCenterUserList.stream().noneMatch(d -> d.getUserId().equals(e)))
         .collect(Collectors.toList());
      if (!insertUserIdList.isEmpty()) {
         List<CoreDataCenterUserBean> insertDataCenterUserList = insertUserIdList.stream().map(e -> {
            CoreDataCenterUserBean insertDataCenterUser = new CoreDataCenterUserBean();
            insertDataCenterUser.setId(ApplicationContextHelper.getNextIdentity());
            insertDataCenterUser.setUserId(e);
            insertDataCenterUser.setDataCenterId(dataCenterId);
            insertDataCenterUser.setActivatedFlag("1");
            return insertDataCenterUser;
         }).collect(Collectors.toList());
         this.getDao().insert(insertDataCenterUserList);
      }
   }

   private void doAfterRoleAddUser(List<CoreDataCenterRoleBean> dataCenterRoleList, List<CoreRoleUserBean> roleUserList) {
      List<Long> dataCenterIdList = dataCenterRoleList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList());
      List<String> userIdList = roleUserList.stream().map(e -> e.getUserId()).distinct().collect(Collectors.toList());
      List<CoreDataCenterUserBean> insertDataCenterUserList = new ArrayList<>();

      for (Long dataCenterId : dataCenterIdList) {
         for (String userId : userIdList) {
            CoreDataCenterUserBean insertDataCenterUser = new CoreDataCenterUserBean();
            insertDataCenterUser.setUserId(userId);
            insertDataCenterUser.setDataCenterId(dataCenterId);
            insertDataCenterUser.setActivatedFlag("1");
            insertDataCenterUserList.add(insertDataCenterUser);
         }
      }

      List<CoreDataCenterUserBean> dataCenterUserList = this.getDao()
         .selectList(insertDataCenterUserList, Arrays.asList("USERID", "DATACENTERID"), CollectionUtils.emptyList(), new Order[0]);
      List<CoreDataCenterUserBean> finalInsertDataCenterUserList = insertDataCenterUserList.stream()
         .filter(e -> dataCenterUserList.stream().noneMatch(d -> d.getDataCenterId() == e.getDataCenterId() && d.getUserId().equals(e.getUserId())))
         .collect(Collectors.toList());
      if (!finalInsertDataCenterUserList.isEmpty()) {
         finalInsertDataCenterUserList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
         this.getDao().insert(finalInsertDataCenterUserList);
      }
   }

   private void doAfterRoleDeleteUser(List<CoreDataCenterRoleBean> dataCenterRoleList, List<CoreRoleUserBean> roleUserList, List<Long> roleIdList) {
      List<String> deleteUserIdList = roleUserList.stream().map(e -> e.getUserId()).collect(Collectors.toList());
      List<Long> dataCenterIdList = dataCenterRoleList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList());
      List<CoreDataCenterRoleUserDTO> dataCenterRoleUserDTOList = this.coreDataCenterRoleService
         .selectDataCenterRoleUserListByFilter(
            SearchFilter.instance().match("DATACENTERID", dataCenterIdList).filter(MatchPattern.OR).match("USERID", deleteUserIdList).filter(MatchPattern.OR)
         );
      List<CoreDataCenterUserBean> deleteDataCenterUserList = new ArrayList<>();

      for (Long dataCenterId : dataCenterIdList) {
         List<CoreDataCenterRoleUserDTO> currentDataCenterRoleUserDTOList = dataCenterRoleUserDTOList.stream()
            .filter(e -> e.getDataCenterId() == dataCenterId)
            .collect(Collectors.toList());

         for (String deleteUserId : deleteUserIdList) {
            if (currentDataCenterRoleUserDTOList.stream().filter(e -> e.getUserId().equals(deleteUserId)).count() < 2L) {
               CoreDataCenterUserBean deleteDataCenterUser = new CoreDataCenterUserBean();
               deleteDataCenterUser.setUserId(deleteUserId);
               deleteDataCenterUser.setDataCenterId(dataCenterId);
               deleteDataCenterUserList.add(deleteDataCenterUser);
            }
         }
      }

      if (!deleteDataCenterUserList.isEmpty()) {
         this.getDao().deleteBy(deleteDataCenterUserList, new String[]{"DATACENTERID", "USERID"});
      }
   }

   private void doAfterUserAddRole(List<CoreDataCenterRoleBean> dataCenterRoleList, List<CoreRoleUserBean> roleUserList) {
      String userId = roleUserList.get(0).getUserId();
      List<Long> dataCenterIdList = dataCenterRoleList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList());
      List<CoreDataCenterUserBean> insertDataCenterUserList = dataCenterIdList.stream().map(e -> {
         CoreDataCenterUserBean insertDataCenterUser = new CoreDataCenterUserBean();
         insertDataCenterUser.setId(ApplicationContextHelper.getNextIdentity());
         insertDataCenterUser.setUserId(userId);
         insertDataCenterUser.setDataCenterId(e);
         insertDataCenterUser.setActivatedFlag("1");
         return insertDataCenterUser;
      }).collect(Collectors.toList());
      this.getDao().insert(insertDataCenterUserList);
   }

   private void doAfterUserDeleteRole(List<CoreDataCenterRoleBean> dataCenterRoleList, List<CoreRoleUserBean> roleUserList) {
      String userId = roleUserList.get(0).getUserId();
      List<Long> dataCenterIdList = dataCenterRoleList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList());
      List<CoreDataCenterUserBean> deleteDataCenterUserList = dataCenterIdList.stream().map(e -> {
         CoreDataCenterUserBean deleteDataCenterUser = new CoreDataCenterUserBean();
         deleteDataCenterUser.setUserId(userId);
         deleteDataCenterUser.setDataCenterId(e);
         return deleteDataCenterUser;
      }).collect(Collectors.toList());
      if (!deleteDataCenterUserList.isEmpty()) {
         this.getDao().deleteBy(deleteDataCenterUserList, new String[]{"DATACENTERID", "USERID"});
      }
   }
}
