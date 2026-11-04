package com.sunwayworld.module.mdm.user.dao.audit;

import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.at.handler.AuditTrailRelationTableDaoHandler;
import com.sunwayworld.framework.at.handler.RelationTableHandlerContext;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.mybatis.dao.event.DaoEventType;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditTrailCoreUserPermissionDaoHandler implements AuditTrailRelationTableDaoHandler {
   @Override
   public void handleInsert(RelationTableHandlerContext context) {
      List<CoreUserPermissionBean> items = context.getItems();
      this.addAuditTrail(items, DaoEventType.insert);
   }

   @Override
   public void handleDelete(RelationTableHandlerContext context) {
      List<CoreUserPermissionBean> items = context.getItems();
      this.addAuditTrail(items, DaoEventType.delete);
   }

   @Override
   public String tableName() {
      return PersistableMetadataHelper.getTableName(CoreUserPermissionBean.class);
   }

   public void addAuditTrail(List<CoreUserPermissionBean> itemList, DaoEventType eventType) {
      if (AuditTrailHelper.auditTrail()) {
         Map<String, Map<String, List<CoreUserPermissionBean>>> userPermissionTypeMapMap = itemList.stream()
            .collect(Collectors.groupingBy(CoreUserPermissionBean::getUserId, Collectors.groupingBy(CoreUserPermissionBean::getPermissionTypeId)));
         AuditDatabaseOperation auditDatabaseOperation = AuditTrailHelper.toOperation(eventType);
         String recordType = AuditTrailHelper.toSqlType(eventType).name();
         userPermissionTypeMapMap.forEach((userId, permissionTypeMap) -> {
            String tableName = PersistableMetadataHelper.getTableName(CoreUserBean.class);
            if (AuditTrailHelper.auditTrailable(tableName, auditDatabaseOperation)) {
               String targetId = tableName + "$" + userId;
               permissionTypeMap.forEach((permissionType, permissionBeans) -> {
                  List<CoreAuditTrailRecordLineBean> coreAuditTrailRecordLineBeans = new ArrayList<>(permissionBeans.size());

                  for (CoreUserPermissionBean permissionBean : permissionBeans) {
                     CoreAuditTrailRecordLineBean trailRecordLineBean = new CoreAuditTrailRecordLineBean();
                     trailRecordLineBean.setTargetId(targetId);
                     trailRecordLineBean.setRecordType(recordType);
                     trailRecordLineBean.setTableName(tableName);
                     trailRecordLineBean.setColumn(permissionType);
                     AuditTrailHelper.setRecordLineValue(trailRecordLineBean, permissionBean.getTargetId(), eventType);
                     coreAuditTrailRecordLineBeans.add(trailRecordLineBean);
                  }

                  AuditTrailAspect.addRecordLine(targetId, coreAuditTrailRecordLineBeans);
               });
            }
         });
      }
   }
}
