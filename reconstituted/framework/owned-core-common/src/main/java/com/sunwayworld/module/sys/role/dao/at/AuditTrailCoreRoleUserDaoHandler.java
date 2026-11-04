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
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuditTrailCoreRoleUserDaoHandler
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
        return PersistableMetadataHelper.getTableName(CoreRoleUserBean.class);
    }

    public void addAuditTrail(List<CoreRoleUserBean> itemList, DaoEventType eventType) {
        if (!AuditTrailHelper.auditTrail()) {
            return;
        }
        Map<Long, List<CoreRoleUserBean>> roleIdUsersMap = itemList.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getRoleId));
        ArrayList<Long> roleIds = new ArrayList<Long>(roleIdUsersMap.keySet());
        Map<String, List<CoreRoleUserBean>> userIdRolesMap = itemList.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getUserId));
        ArrayList<String> userIds = new ArrayList<String>(userIdRolesMap.keySet());
        AuditDatabaseOperation operation = AuditTrailHelper.toOperation(eventType);
        String roleTable = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
        if (!AuditTrailHelper.auditTrailable(roleTable, operation)) {
            return;
        }
        String recordType = AuditTrailHelper.toSqlType(eventType).name();
        for (Long coreRoleBeanId : roleIds) {
            String targetId = roleTable + "$" + coreRoleBeanId;
            List<CoreRoleUserBean> coreRoleUserBeans = roleIdUsersMap.get(coreRoleBeanId);
            if (CollectionUtils.isEmpty(coreRoleUserBeans)) continue;
            ArrayList<CoreAuditTrailRecordLineBean> coreAuditTrailRecordLineBeans = new ArrayList<CoreAuditTrailRecordLineBean>(coreRoleUserBeans.size());
            for (CoreRoleUserBean coreRoleUserBean : coreRoleUserBeans) {
                CoreAuditTrailRecordLineBean trailRecordLineBean = new CoreAuditTrailRecordLineBean();
                trailRecordLineBean.setTargetId(targetId);
                trailRecordLineBean.setRecordType(recordType);
                trailRecordLineBean.setTableName(roleTable);
                trailRecordLineBean.setColumn("USER");
                AuditTrailHelper.setRecordLineValue(trailRecordLineBean, coreRoleUserBean.getUserId(), eventType);
                coreAuditTrailRecordLineBeans.add(trailRecordLineBean);
            }
            AuditTrailAspect.addRecordLine(targetId, coreAuditTrailRecordLineBeans);
        }
        String userTable = PersistableMetadataHelper.getTableName(CoreUserBean.class);
        if (!AuditTrailHelper.auditTrailable(userTable, operation)) {
            return;
        }
        for (String coreUserBeanId : userIds) {
            String targetId = userTable + "$" + coreUserBeanId;
            List<CoreRoleUserBean> coreRoleUserBeans = userIdRolesMap.get(coreUserBeanId);
            if (CollectionUtils.isEmpty(coreRoleUserBeans)) continue;
            ArrayList<CoreAuditTrailRecordLineBean> coreAuditTrailRecordLineBeans = new ArrayList<CoreAuditTrailRecordLineBean>(coreRoleUserBeans.size());
            for (CoreRoleUserBean coreRoleUserBean : coreRoleUserBeans) {
                CoreAuditTrailRecordLineBean trailRecordLineBean = new CoreAuditTrailRecordLineBean();
                trailRecordLineBean.setTargetId(targetId);
                trailRecordLineBean.setRecordType(recordType);
                trailRecordLineBean.setTableName(userTable);
                trailRecordLineBean.setColumn("ROLE");
                AuditTrailHelper.setRecordLineValue(trailRecordLineBean, coreRoleUserBean.getRoleId().toString(), eventType);
                coreAuditTrailRecordLineBeans.add(trailRecordLineBean);
            }
            AuditTrailAspect.addRecordLine(targetId, coreAuditTrailRecordLineBeans);
        }
    }
}
