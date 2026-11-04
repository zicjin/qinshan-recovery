/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.constant;

public class AuditPathConstant {
    public static final String AUDIT_CONFIG_PATH = "/secure/core/module/sys/audit/audit-configs";
    public static final String AUDIT_LOG_PATH = "/secure/core/module/sys/audit/audit-logs";
    public static final String AUDIT_LOG_ROOT_ITEMS_PATH = "/secure/core/module/sys/audit/audit-logs/items?table={table}&auditLogId={auditLogId}";
    public static final String AUDIT_LOG_ITEMS_PATH = "/secure/core/module/sys/audit/audit-logs/items?table={table}&parentAuditLogId={parentAuditLogId}";
    public static final String AUDIT_LOG_ROOT_ITEM_VERTICAL_ACTIVITIES_PATH = "/secure/core/module/sys/audit/audit-logs/{auditLogId}/vertical-activities";
    public static final String AUDIT_LOG_ROOT_ITEM_ACTIVITY_VERTICAL_COLUMNS_PATH = "/secure/core/module/sys/audit/audit-logs/activities/{activityId}/vertical-columns";
    public static final String AUDIT_LOG_ROOT_ITEM_HORIZONTAL_ACTIVITIES_PATH = "/secure/core/module/sys/audit/audit-logs/{auditLogId}/horizontal-activities";
}
