package com.sunwayworld.module.mdm.user.dao.audit;

import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableDaoHandler;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableHandlerContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditV2CoreUserPermissionDaoHandler implements AuditV2RelationTableDaoHandler {
   @Override
   public void handleInsert(AuditV2RelationTableHandlerContext context) {
      String tableName = PersistableMetadataHelper.getTableName(CoreUserBean.class);
      List<CoreUserPermissionBean> items = context.getItems();
      Map<String, Map<String, List<CoreUserPermissionBean>>> userPermissionTypeMapMap = items.stream()
         .collect(Collectors.groupingBy(CoreUserPermissionBean::getUserId, Collectors.groupingBy(CoreUserPermissionBean::getPermissionTypeId)));
      userPermissionTypeMapMap.forEach((userId, permissionTypeMap) -> permissionTypeMap.forEach((permissionType, permissionBeans) -> {
         List<String> targetIds = permissionBeans.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", userId);
         newValueMap.put(permissionType, targetIds);
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", userId);
         AuditLogHelper.insertAuditLog(tableName, oldValueMap, newValueMap);
      }));
   }

   @Override
   public void handleDelete(AuditV2RelationTableHandlerContext context) {
      String tableName = PersistableMetadataHelper.getTableName(CoreUserBean.class);
      List<CoreUserPermissionBean> items = context.getItems();
      Map<String, Map<String, List<CoreUserPermissionBean>>> userPermissionTypeMapMap = items.stream()
         .collect(Collectors.groupingBy(CoreUserPermissionBean::getUserId, Collectors.groupingBy(CoreUserPermissionBean::getPermissionTypeId)));
      userPermissionTypeMapMap.forEach((userId, permissionTypeMap) -> permissionTypeMap.forEach((permissionType, permissionBeans) -> {
         List<String> targetIds = permissionBeans.stream().map(CoreUserPermissionBean::getTargetId).collect(Collectors.toList());
         CaseInsensitiveLinkedMap<Object> oldValueMap = new CaseInsensitiveLinkedMap<>();
         oldValueMap.put("id", userId);
         oldValueMap.put(permissionType, targetIds);
         CaseInsensitiveLinkedMap<Object> newValueMap = new CaseInsensitiveLinkedMap<>();
         newValueMap.put("id", userId);
         AuditLogHelper.insertAuditLog(tableName, oldValueMap, newValueMap);
      }));
   }

   @Override
   public String tableName() {
      return PersistableMetadataHelper.getTableName(CoreUserPermissionBean.class);
   }
}
