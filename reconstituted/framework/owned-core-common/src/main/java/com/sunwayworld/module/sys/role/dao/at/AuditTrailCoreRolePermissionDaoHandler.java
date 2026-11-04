/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.dao.at;

import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.at.handler.AuditTrailRelationTableDaoHandler;
import com.sunwayworld.framework.at.handler.RelationTableHandlerContext;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.mybatis.dao.event.DaoEventType;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditTrailCoreRolePermissionDaoHandler
implements AuditTrailRelationTableDaoHandler {
    @Override
    public void handleInsert(RelationTableHandlerContext context) {
        this.addAuditTrail(context.getItems(), DaoEventType.insert);
    }

    @Override
    public void handleDelete(RelationTableHandlerContext context) {
        this.addAuditTrail(context.getItems(), DaoEventType.delete);
    }

    @Override
    public String tableName() {
        return PersistableMetadataHelper.getTableName(CoreRolePermissionBean.class);
    }

    public void addAuditTrail(List<CoreRolePermissionBean> itemList, DaoEventType eventType) {
        if (!AuditTrailHelper.auditTrail()) {
            return;
        }
        Map<Long, Map<String, List<CoreRolePermissionBean>>> rolePermissionTypeMapMap = itemList.stream().collect(Collectors.groupingBy(CoreRolePermissionBean::getRoleId, Collectors.groupingBy(CoreRolePermissionBean::getPermissionTypeId)));
        AuditDatabaseOperation auditDatabaseOperation = AuditTrailHelper.toOperation(eventType);
        String recordType = AuditTrailHelper.toSqlType(eventType).name();
        rolePermissionTypeMapMap.forEach((roleId, permissionTypeMap) -> {
            String tableName = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
            if (!AuditTrailHelper.auditTrailable(tableName, auditDatabaseOperation)) {
                return;
            }
            String targetId = tableName + "$" + roleId;
            permissionTypeMap.forEach((permissionType, permissionBeans) -> {
                ArrayList<CoreAuditTrailRecordLineBean> coreAuditTrailRecordLineBeans = new ArrayList<CoreAuditTrailRecordLineBean>(permissionBeans.size());
                for (CoreRolePermissionBean permissionBean : permissionBeans) {
                    CoreAuditTrailRecordLineBean trailRecordLineBean = new CoreAuditTrailRecordLineBean();
                    trailRecordLineBean.setTargetId(targetId);
                    trailRecordLineBean.setRecordType(recordType);
                    trailRecordLineBean.setTableName(tableName);
                    trailRecordLineBean.setColumn((String)permissionType);
                    AuditTrailHelper.setRecordLineValue(trailRecordLineBean, permissionBean.getTargetId(), eventType);
                    coreAuditTrailRecordLineBeans.add(trailRecordLineBean);
                }
                AuditTrailAspect.addRecordLine(targetId, coreAuditTrailRecordLineBeans);
            });
        });
    }
}
