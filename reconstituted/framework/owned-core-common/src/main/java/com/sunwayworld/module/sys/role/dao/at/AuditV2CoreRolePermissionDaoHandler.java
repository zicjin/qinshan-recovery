package com.sunwayworld.module.sys.role.dao.at;

import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableDaoHandler;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableHandlerContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditV2CoreRolePermissionDaoHandler implements AuditV2RelationTableDaoHandler {
   @Override
   public void handleInsert(AuditV2RelationTableHandlerContext context) {
      String tableName = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
      List<CoreRolePermissionBean> items = context.getItems();
      Map<Long, Map<String, List<CoreRolePermissionBean>>> rolePermissionTypeMapMap = items.stream()
         .collect(Collectors.groupingBy(CoreRolePermissionBean::getRoleId, Collectors.groupingBy(CoreRolePermissionBean::getPermissionTypeId)));
      rolePermissionTypeMapMap.forEach((roleId, permissionTypeMap) -> permissionTypeMap.forEach((permissionType, permissionBeans) -> {
         List<String> targetIds = permissionBeans.stream().map(CoreRolePermissionBean::getTargetId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", roleId);
         newValueMap.put(permissionType, targetIds);
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", roleId);
         AuditLogHelper.insertAuditLog(tableName, oldValueMap, newValueMap);
      }));
   }

   @Override
   public void handleDelete(AuditV2RelationTableHandlerContext context) {
      String tableName = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
      List<CoreRolePermissionBean> items = context.getItems();
      Map<Long, Map<String, List<CoreRolePermissionBean>>> rolePermissionTypeMapMap = items.stream()
         .collect(Collectors.groupingBy(CoreRolePermissionBean::getRoleId, Collectors.groupingBy(CoreRolePermissionBean::getPermissionTypeId)));
      rolePermissionTypeMapMap.forEach((roleId, permissionTypeMap) -> permissionTypeMap.forEach((permissionType, permissionBeans) -> {
         List<String> targetIds = permissionBeans.stream().map(CoreRolePermissionBean::getTargetId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", roleId);
         oldValueMap.put(permissionType, targetIds);
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", roleId);
         AuditLogHelper.insertAuditLog(tableName, oldValueMap, newValueMap);
      }));
   }

   @Override
   public String tableName() {
      return PersistableMetadataHelper.getTableName(CoreRolePermissionBean.class);
   }
}
