/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.support.helper;

import com.sunwayworld.framework.exception.UnsupportedException;
import java.util.List;

public class AuditSqlHelper {
    public static final String OLD_AUDIT_COMMENT = "-- \u65e7\u7248\u5ba1\u8ba1\u8ddf\u8e2a\uff0c\u8ddf\u8e2a\u914d\u7f6e\u53d8\u66f4\u8bed\u53e5\n";
    public static final String AUDIT_V2_COMMENT = "-- \u5ba1\u8ba1\u8ddf\u8e2aV2\uff0c\u8ddf\u8e2a\u914d\u7f6e\u53d8\u66f4\u8bed\u53e5\n";
    public static final String NEWLINE = "\n";
    public static final String COMMIT = "COMMIT;";

    public static void generateAuditTrailSql(String tableName, String userId, String userName, List<String> addFieldList, List<String> removeFieldList, StringBuilder oracleAuditSql, StringBuilder mysqlAuditSql, StringBuilder sqlserverAuditSql, StringBuilder pgsqlAuditSql) {
        AuditSqlHelper.addAuditTrailComment(oracleAuditSql, mysqlAuditSql, sqlserverAuditSql, pgsqlAuditSql);
        for (String field : removeFieldList) {
            AuditSqlHelper.generateDeleteAuditTrailSql(tableName, field, oracleAuditSql);
            AuditSqlHelper.generateDeleteAuditTrailSql(tableName, field, mysqlAuditSql);
            AuditSqlHelper.generateDeleteAuditTrailSql(tableName, field, sqlserverAuditSql);
            AuditSqlHelper.generateDeleteAuditTrailSql(tableName, field, pgsqlAuditSql);
        }
        if (!removeFieldList.isEmpty()) {
            oracleAuditSql.append(COMMIT);
            mysqlAuditSql.append(COMMIT);
            pgsqlAuditSql.append(COMMIT);
        }
        oracleAuditSql.append(NEWLINE);
        mysqlAuditSql.append(NEWLINE);
        sqlserverAuditSql.append(NEWLINE);
        pgsqlAuditSql.append(NEWLINE);
        for (String field : addFieldList) {
            AuditSqlHelper.generateInsertAuditTrailSql("SEQ_CORE.NEXTVAL", tableName, field, userId, userName, AuditSqlHelper.getDateNow("oracle"), oracleAuditSql);
            AuditSqlHelper.generateInsertAuditTrailSql("F_GET_SEQ_CORE()", tableName, field, userId, userName, AuditSqlHelper.getDateNow("mysql"), mysqlAuditSql);
            AuditSqlHelper.generateInsertAuditTrailSql("NEXT VALUE FOR SEQ_CORE", tableName, field, userId, userName, AuditSqlHelper.getDateNow("sqlserver"), sqlserverAuditSql);
            AuditSqlHelper.generateInsertAuditTrailSql("NEXTVAL('seq_core')", tableName, field, userId, userName, AuditSqlHelper.getDateNow("pgsql"), pgsqlAuditSql);
        }
        if (!addFieldList.isEmpty()) {
            oracleAuditSql.append(COMMIT).append(NEWLINE);
            mysqlAuditSql.append(COMMIT).append(NEWLINE);
            pgsqlAuditSql.append(COMMIT).append(NEWLINE);
        }
    }

    public static void generateAuditV2Sql(String tableName, List<String> addFieldList, List<String> removeFieldList, StringBuilder oracleAuditSqlBuilder, StringBuilder mysqlAuditSqlBuilder, StringBuilder sqlserverAuditSqlBuilder, StringBuilder pgsqlAuditSqlBuilder) {
        AuditSqlHelper.addAuditV2Comment(oracleAuditSqlBuilder, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder);
        for (String field : removeFieldList) {
            AuditSqlHelper.generateDeleteAuditV2Sql(AuditSqlHelper.getColumnColumn("oracle"), tableName, field, oracleAuditSqlBuilder);
            AuditSqlHelper.generateDeleteAuditV2Sql(AuditSqlHelper.getColumnColumn("mysql"), tableName, field, mysqlAuditSqlBuilder);
            AuditSqlHelper.generateDeleteAuditV2Sql(AuditSqlHelper.getColumnColumn("sqlserver"), tableName, field, sqlserverAuditSqlBuilder);
            AuditSqlHelper.generateDeleteAuditV2Sql(AuditSqlHelper.getColumnColumn("pgsql"), tableName, field, pgsqlAuditSqlBuilder);
        }
        if (!removeFieldList.isEmpty()) {
            oracleAuditSqlBuilder.append(COMMIT);
            mysqlAuditSqlBuilder.append(COMMIT);
            pgsqlAuditSqlBuilder.append(COMMIT);
        }
        oracleAuditSqlBuilder.append(NEWLINE);
        mysqlAuditSqlBuilder.append(NEWLINE);
        sqlserverAuditSqlBuilder.append(NEWLINE);
        pgsqlAuditSqlBuilder.append(NEWLINE);
        for (String field : addFieldList) {
            AuditSqlHelper.generateInsertAuditV2Sql(AuditSqlHelper.getColumnColumn("oracle"), "SEQ_CORE.NEXTVAL", tableName, field, oracleAuditSqlBuilder);
            AuditSqlHelper.generateInsertAuditV2Sql(AuditSqlHelper.getColumnColumn("mysql"), "F_GET_SEQ_CORE()", tableName, field, mysqlAuditSqlBuilder);
            AuditSqlHelper.generateInsertAuditV2Sql(AuditSqlHelper.getColumnColumn("sqlserver"), "NEXT VALUE FOR SEQ_CORE", tableName, field, sqlserverAuditSqlBuilder);
            AuditSqlHelper.generateInsertAuditV2Sql(AuditSqlHelper.getColumnColumn("pgsql"), "NEXTVAL('seq_core')", tableName, field, pgsqlAuditSqlBuilder);
        }
        if (!addFieldList.isEmpty()) {
            oracleAuditSqlBuilder.append(COMMIT).append(NEWLINE);
            mysqlAuditSqlBuilder.append(COMMIT).append(NEWLINE);
            pgsqlAuditSqlBuilder.append(COMMIT).append(NEWLINE);
        }
    }

    private static void generateInsertAuditTrailSql(String id, String tableName, String field, String userId, String userName, String sqlNow, StringBuilder auditSql) {
        String insertSql = String.format("INSERT INTO T_CORE_AT_CONFIG_LINE (ID,CONFIGID,COLUMNID,AUDITTRAIL,CREATEDBYID,CREATEDBYNAME,CREATEDTIME) VALUES (%s,'%s','%s','%s','%s','%s', %s);\n", id, tableName, field, "1", userId, userName, sqlNow);
        auditSql.append(insertSql);
    }

    private static void generateDeleteAuditTrailSql(String tableName, String field, StringBuilder auditSql) {
        String deleteSql = String.format("DELETE FROM T_CORE_AT_CONFIG_LINE WHERE CONFIGID='%s' AND COLUMNID='%s';\n", tableName, field);
        auditSql.append(deleteSql);
    }

    private static void generateInsertAuditV2Sql(String columnColumn, String id, String tableName, String field, StringBuilder auditSql) {
        String insertSql = String.format("INSERT INTO T_CORE_AUDIT_CONFIG_COLUMN (ID,CONFIGID,%s,AUDITABLE) VALUES (%s,'%s','%s','%s');\n", columnColumn, id, tableName, field, "1");
        auditSql.append(insertSql);
    }

    private static void generateDeleteAuditV2Sql(String columnColumn, String tableName, String field, StringBuilder auditSql) {
        String deleteSql = String.format("DELETE FROM T_CORE_AUDIT_CONFIG_COLUMN WHERE CONFIGID='%s' AND %s='%s';\n", tableName, columnColumn, field);
        auditSql.append(deleteSql);
    }

    private static String getDateNow(String dbType) {
        if ("mysql".equals(dbType)) {
            return "NOW()";
        }
        if ("oracle".equals(dbType)) {
            return "CURRENT_TIMESTAMP";
        }
        if ("sqlserver".equals(dbType)) {
            return "GETDATE()";
        }
        if ("pgsql".equals(dbType)) {
            return "NOW()";
        }
        throw new UnsupportedException("unsupported db type: " + dbType);
    }

    private static String getColumnColumn(String dbType) {
        if ("mysql".equals(dbType)) {
            return "`COLUMN`";
        }
        if ("pgsql".equals(dbType)) {
            return "\"column\"";
        }
        return "\"COLUMN\"";
    }

    private static void addAuditTrailComment(StringBuilder oracleAuditSql, StringBuilder mysqlAuditSqlBuilder, StringBuilder sqlserverAuditSqlBuilder, StringBuilder pgsqlAuditSqlBuilder) {
        oracleAuditSql.append(OLD_AUDIT_COMMENT);
        mysqlAuditSqlBuilder.append(OLD_AUDIT_COMMENT);
        sqlserverAuditSqlBuilder.append(OLD_AUDIT_COMMENT);
        pgsqlAuditSqlBuilder.append(OLD_AUDIT_COMMENT);
    }

    private static void addAuditV2Comment(StringBuilder oracleAuditSql, StringBuilder mysqlAuditSqlBuilder, StringBuilder sqlserverAuditSqlBuilder, StringBuilder pgsqlAuditSqlBuilder) {
        oracleAuditSql.append(NEWLINE).append(AUDIT_V2_COMMENT);
        mysqlAuditSqlBuilder.append(NEWLINE).append(AUDIT_V2_COMMENT);
        sqlserverAuditSqlBuilder.append(NEWLINE).append(AUDIT_V2_COMMENT);
        pgsqlAuditSqlBuilder.append(NEWLINE).append(AUDIT_V2_COMMENT);
    }

    public static void generateAuditTrailConfigSql(String tableName, String tableDesc, String userId, String userName, StringBuilder oracleAuditSqlBuilder, StringBuilder mysqlAuditSqlBuilder, StringBuilder sqlserverAuditSqlBuilder, StringBuilder pgsqlAuditSqlBuilder) {
        String configSqlTemplate = "INSERT INTO T_CORE_AT_CONFIG (ID, AUDITTRAIL, CREATEDBYID, CREATEDBYNAME, CREATEDTIME)\nVALUES ('%s', '1', '%s', '%s', %s);";
        oracleAuditSqlBuilder.append(NEWLINE).append(String.format(configSqlTemplate, tableName, userId, userName, AuditSqlHelper.getDateNow("oracle"))).append(NEWLINE);
        mysqlAuditSqlBuilder.append(NEWLINE).append(String.format(configSqlTemplate, tableName, userId, userName, AuditSqlHelper.getDateNow("mysql"))).append(NEWLINE);
        sqlserverAuditSqlBuilder.append(NEWLINE).append(String.format(configSqlTemplate, tableName, userId, userName, AuditSqlHelper.getDateNow("sqlserver"))).append(NEWLINE);
        pgsqlAuditSqlBuilder.append(NEWLINE).append(String.format(configSqlTemplate, tableName, userId, userName, AuditSqlHelper.getDateNow("pgsql"))).append(NEWLINE);
    }

    public static void generateAuditV2ConfigSql(String tableName, String tableDesc, StringBuilder oracleAuditSqlBuilder, StringBuilder mysqlAuditSqlBuilder, StringBuilder sqlserverAuditSqlBuilder, StringBuilder pgsqlAuditSqlBuilder) {
        String configSqlTemplate = "INSERT INTO T_CORE_AUDIT_CONFIG (ID, NAME, CATEGORY, AUDITABLE)\nVALUES ('%s', '%s', 'service', '1');";
        String configSql = String.format(configSqlTemplate, tableName, tableDesc);
        oracleAuditSqlBuilder.append(NEWLINE).append(configSql).append(NEWLINE);
        mysqlAuditSqlBuilder.append(NEWLINE).append(configSql).append(NEWLINE);
        sqlserverAuditSqlBuilder.append(NEWLINE).append(configSql).append(NEWLINE);
        pgsqlAuditSqlBuilder.append(NEWLINE).append(configSql).append(NEWLINE);
    }
}
