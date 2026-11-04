/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.at;

import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.SqlType;
import com.sunwayworld.framework.mybatis.dao.event.DaoEventType;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditTrailHelper {
    private static CoreAuditTrailConfigService configService;

    @Autowired
    public void setCoreAuditTrailConfigService(CoreAuditTrailConfigService configService) {
        AuditTrailHelper.configService = configService;
    }

    public static boolean auditTrailable(String table, AuditDatabaseOperation auditDatabaseOperation) {
        if (!ApplicationContextHelper.getEnvironment().getProperty(auditDatabaseOperation.getPropertyKey(), Boolean.class).booleanValue()) {
            return false;
        }
        return configService.auditTrailable(table);
    }

    public static boolean auditTrailableColumn(String table, String column) {
        List<CoreAuditTrailConfigLineBean> configLineList = configService.selectAuditTrailableLineList(table);
        return configLineList.stream().anyMatch(l -> l.getColumnId().equalsIgnoreCase(column));
    }

    public static boolean auditTrail() {
        return ApplicationContextHelper.getEnvironment().acceptsProfiles(p -> p.test("audit-trail"));
    }

    public static AuditDatabaseOperation toOperation(DaoEventType eventType) {
        AuditDatabaseOperation operation = null;
        switch (eventType) {
            case insert: {
                operation = AuditDatabaseOperation.INSERT;
                break;
            }
            case update: {
                operation = AuditDatabaseOperation.UPDATE;
                break;
            }
            case delete: {
                operation = AuditDatabaseOperation.DELETE;
            }
        }
        return operation;
    }

    public static SqlType toSqlType(DaoEventType eventType) {
        SqlType sqlType = null;
        switch (eventType) {
            case insert: {
                sqlType = SqlType.INSERT;
                break;
            }
            case update: {
                sqlType = SqlType.UPDATE;
                break;
            }
            case delete: {
                sqlType = SqlType.DELETE;
            }
        }
        return sqlType;
    }

    public static void setRecordLineValue(CoreAuditTrailRecordLineBean trailRecordLineBean, String value, DaoEventType eventType) {
        if (eventType == DaoEventType.delete) {
            trailRecordLineBean.setOldValue(value);
        }
        if (eventType == DaoEventType.insert) {
            trailRecordLineBean.setNewValue(value);
        }
    }
}
