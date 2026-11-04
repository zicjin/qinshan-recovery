package com.sunwayworld.module.sys.role.dao.at;

import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableDaoHandler;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableHandlerContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditV2CoreRoleUserDaoHandler implements AuditV2RelationTableDaoHandler {
   @Override
   public void handleInsert(AuditV2RelationTableHandlerContext context) {
      List<CoreRoleUserBean> items = context.getItems();
      String roleTable = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
      String userTable = PersistableMetadataHelper.getTableName(CoreUserBean.class);
      Map<Long, List<CoreRoleUserBean>> roleUsersMap = items.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getRoleId));
      roleUsersMap.forEach((roleId, users) -> {
         List<String> userIds = users.stream().map(CoreRoleUserBean::getUserId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", roleId);
         newValueMap.put(userTable, userIds);
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", roleId);
         AuditLogHelper.insertAuditLog(roleTable, oldValueMap, newValueMap);
      });
      Map<String, List<CoreRoleUserBean>> userRolesMap = items.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getUserId));
      userRolesMap.forEach((userId, roles) -> {
         List<Long> roleIds = roles.stream().map(CoreRoleUserBean::getRoleId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", userId);
         newValueMap.put(roleTable, roleIds);
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", userId);
         AuditLogHelper.insertAuditLog(userTable, oldValueMap, newValueMap);
      });
   }

   @Override
   public void handleDelete(AuditV2RelationTableHandlerContext context) {
      List<CoreRoleUserBean> items = context.getItems();
      String roleTable = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
      String userTable = PersistableMetadataHelper.getTableName(CoreUserBean.class);
      Map<Long, List<CoreRoleUserBean>> roleUsersMap = items.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getRoleId));
      roleUsersMap.forEach((roleId, users) -> {
         List<String> userIds = users.stream().map(CoreRoleUserBean::getUserId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", roleId);
         oldValueMap.put(userTable, userIds);
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", roleId);
         AuditLogHelper.insertAuditLog(roleTable, oldValueMap, newValueMap);
      });
      Map<String, List<CoreRoleUserBean>> userRolesMap = items.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getUserId));
      userRolesMap.forEach((userId, roles) -> {
         List<Long> roleIds = roles.stream().map(CoreRoleUserBean::getRoleId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", userId);
         oldValueMap.put(roleTable, roleIds);
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", userId);
         AuditLogHelper.insertAuditLog(userTable, oldValueMap, newValueMap);
      });
   }

   @Override
   public String tableName() {
      return PersistableMetadataHelper.getTableName(CoreRoleUserBean.class);
   }
}
