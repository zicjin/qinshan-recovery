/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.support.helper;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.cloud.module.codegenerator.support.helper.DbMetaCheckHelper;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class SqlHelper {
    public String insertMenu(String id, String menuName, String href, String parentId, String orderNo, String menuIcon, String menuInitName) {
        String sql = "INSERT INTO T_CORE_MENU (ID, MENUNAME, HREF, PARENTID, ORDERNO, ICON, MENUINITNAME)\r\nVALUES ('" + id + "', '" + menuName + "', '" + href + "', '" + parentId + "', '" + orderNo + "', '" + menuIcon + "', '" + menuInitName + "');\r\n";
        return sql;
    }

    public String insertSysTable(List<CoreCodeGenerateConfigTableBean> tableList, String thirdLevelMenuId, String audit) {
        String tableSql = "";
        String tableHierarchySql = "";
        if (!tableList.isEmpty()) {
            String mainTableName = "";
            int subIndex = 0;
            for (int index = 0; index < tableList.size(); ++index) {
                CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable = tableList.get(index);
                String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
                if (!"main".equals(kbCodeGenerateConfigTable.getType())) {
                    tableSql = tableSql + "INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)\r\nVALUES ('" + tableName + "', '0', '0');\r\n";
                    tableHierarchySql = tableHierarchySql + "INSERT INTO T_CORE_TABLE_HIERARCHY (ID, TABLENAME, MASTERTABLENAME, REFERCOLUMN)\r\nVALUES ('" + thirdLevelMenuId + StringUtils.leftPad(++subIndex + "", 2) + "','" + tableName + "', '" + mainTableName + "', '" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() + "');\r\n";
                    continue;
                }
                mainTableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
                tableSql = tableSql + "INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)\r\nVALUES ('" + tableName + "', '" + audit + "', '0');\r\n";
            }
        }
        return tableSql + (tableHierarchySql.length() > 0 ? "\r\n" + tableHierarchySql : "");
    }

    public String insertTodo(String id, String modelName, String tableName, String type, String enable, String todoStatementId, String menuId) {
        String sql = "INSERT INTO T_CORE_MENU_EXT (ID, SERVICENAME, TABLENAME, TYPE, ENABLE, EXT) \r\nVALUES ('" + id + "','" + modelName + "','" + tableName + "','" + type + "','" + enable + "','{\"todoStmtId\":\"" + todoStatementId + "\"}');\r\n    \r\n";
        return sql;
    }

    public String createI118n(CoreCodeGenerateConfigBean coreCodeGenerateConfigBean, CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String lineBreak = "\r\n";
        String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
        String i118n = "";
        i118n = i118n + tableName + ".SERVICE_NAME=" + kbCodeGenerateConfigTable.getTableDesc() + lineBreak;
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean kbCodeGenerateConfigField = fieldList.get(j);
            i118n = i118n + tableName + "." + kbCodeGenerateConfigField.getField().toUpperCase() + "=" + kbCodeGenerateConfigField.getFieldName() + lineBreak;
        }
        i118n = i118n + lineBreak;
        return i118n;
    }

    public String createOracleSql(CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String tableName = "";
        String tableDesc = "";
        String createSqlStr = "";
        String createOracleAppend = "CREATE TABLE ";
        tableName = kbCodeGenerateConfigTable.getTableName();
        tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        createOracleAppend = this.addWarnInfo(tableName, createOracleAppend);
        ArrayList<CoreCodeGenerateConfigFieldBean> newList = new ArrayList<CoreCodeGenerateConfigFieldBean>();
        createOracleAppend = createOracleAppend + tableName + "( \r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = new CoreCodeGenerateConfigFieldBean();
            BeanUtils.copyProperties(fieldList.get(j), field);
            if ("1".equals(field.getRequire())) {
                field.setRequire(" NULL");
            } else {
                field.setRequire(" NOT NULL");
            }
            if ("".equals(field.getFieldLength()) || field.getFieldLength() == null || "0".equals(field.getFieldLength())) {
                field.setFieldLength("");
            }
            if ("varchar".equals(field.getFieldType())) {
                field.setFieldType("VARCHAR2");
            }
            String fieldName = field.getField().toUpperCase();
            createOracleAppend = this.addWarnInfo(fieldName, createOracleAppend);
            createOracleAppend = j == fieldList.size() - 1 ? ("".equals(field.getFieldLength()) ? createOracleAppend + "    " + fieldName + " " + ("VARCHAR2".equals(field.getFieldType()) ? "VARCHAR2" : ("date".equals(field.getFieldType()) || "datetime".equals(field.getFieldType()) ? "DATE" : field.getFieldType().toUpperCase())) + ("  NULL".equals(field.getRequire()) ? "NULL" : field.getRequire()) + "\r\n );\r\n" : createOracleAppend + "    " + fieldName + " " + ("VARCHAR2".equals(field.getFieldType()) ? "VARCHAR2" : ("date".equals(field.getFieldType()) || "datetime".equals(field.getFieldType()) ? "DATE" : field.getFieldType().toUpperCase())) + "(" + field.getFieldLength() + ") " + ("  NULL".equals(field.getRequire()) ? "NULL" : field.getRequire()) + "\r\n );\r\n") : ("".equals(field.getFieldLength()) ? createOracleAppend + "    " + fieldName + " " + ("VARCHAR2".equals(field.getFieldType()) ? "VARCHAR2" : ("date".equals(field.getFieldType()) || "datetime".equals(field.getFieldType()) ? "DATE" : field.getFieldType().toUpperCase())) + ("  NULL".equals(field.getRequire()) ? "NULL" : field.getRequire()) + ",\r\n" : createOracleAppend + "    " + fieldName + " " + ("VARCHAR2".equals(field.getFieldType()) ? "VARCHAR2" : ("date".equals(field.getFieldType()) || "datetime".equals(field.getFieldType()) ? "DATE" : field.getFieldType().toUpperCase())) + "(" + field.getFieldLength() + ") " + ("  NULL".equals(field.getRequire()) ? "NULL" : field.getRequire()) + ",\r\n");
            newList.add(field);
        }
        createOracleAppend = createOracleAppend + "COMMENT ON TABLE " + tableName.toUpperCase() + " IS '" + tableDesc + "';\r\n";
        for (int a = 0; a < newList.size(); ++a) {
            createOracleAppend = createOracleAppend + "COMMENT ON COLUMN " + tableName.toUpperCase() + "." + ((CoreCodeGenerateConfigFieldBean)newList.get(a)).getField().toUpperCase() + " IS '" + ((CoreCodeGenerateConfigFieldBean)newList.get(a)).getFieldName() + "';\r\n";
        }
        String pkName = "";
        pkName = tableName.contains("_") ? "PK_" + tableName.substring(tableName.indexOf("_") + 1) : "PK_" + tableName;
        pkName = pkName.toUpperCase();
        createOracleAppend = this.addWarnInfo(pkName, createOracleAppend);
        createOracleAppend = createOracleAppend + "\r\nALTER TABLE " + tableName.toUpperCase() + " ADD CONSTRAINT " + pkName + " PRIMARY KEY (ID);\r\n";
        if (!StringUtils.isEmpty(kbCodeGenerateConfigTable.getForeignField()) && !"ID".equals(kbCodeGenerateConfigTable.getForeignField().toUpperCase())) {
            String fkName = "";
            fkName = tableName.contains("_") ? "IDX_" + tableName.substring(tableName.indexOf("_") + 1) + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() : "IDX_" + tableName + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase();
            fkName = fkName.toUpperCase();
            createOracleAppend = this.addWarnInfo(fkName, createOracleAppend);
            createOracleAppend = createOracleAppend + "CREATE INDEX " + fkName + " ON " + tableName.toUpperCase() + " (" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() + ");\r\n";
        }
        createSqlStr = createSqlStr + createOracleAppend + "\r\n";
        return createSqlStr;
    }

    public String createOracleI118nSql(CoreCodeGenerateConfigBean coreCodeGenerateConfigBean, CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        String i18nStr = "";
        i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + ".SERVICE_NAME', '" + tableDesc + "', '" + tableDesc + "', 'zh-CN', '" + tableDesc + "');\r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = fieldList.get(j);
            i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + "." + field.getField().toUpperCase() + "', '" + tableDesc + "\u7684" + field.getFieldName() + "', '" + field.getFieldName() + "', 'zh-CN', '" + field.getFieldName() + "');\r\n";
        }
        return i18nStr;
    }

    public String createMysqlSql(CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String createMysqlStr = "";
        String createMysqlAppend = "CREATE TABLE IF NOT EXISTS ";
        String tableName = kbCodeGenerateConfigTable.getTableName();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        createMysqlAppend = createMysqlAppend + "`" + tableName + "`(\r\n ";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = new CoreCodeGenerateConfigFieldBean();
            BeanUtils.copyProperties(fieldList.get(j), field);
            if ("1".equals(field.getRequire())) {
                field.setRequire(" DEFAULT NULL");
            } else {
                field.setRequire(" NOT NULL");
            }
            if ("".equals(field.getFieldLength()) || field.getFieldLength() == null || "0".equals(field.getFieldLength())) {
                field.setFieldLength("");
            }
            if ("date".equals(field.getFieldType())) {
                field.setFieldType("datetime");
            }
            if ("clob".equals(field.getFieldType())) {
                field.setFieldType("longtext");
            }
            if ("number".equals(field.getFieldType())) {
                if ("id".equals(field.getField())) {
                    field.setFieldType("bigint");
                } else if (field.getFieldLength().indexOf(",") > 0) {
                    field.setFieldType("decimal");
                } else {
                    field.setFieldType("bigint");
                }
            }
            if (j == fieldList.size() - 1) {
                if ("".equals(field.getFieldLength())) {
                    createMysqlAppend = createMysqlAppend + "    `" + field.getField().toUpperCase() + "` " + field.getFieldType().toUpperCase() + field.getRequire() + " COMMENT '" + field.getFieldName() + "',\r\n      PRIMARY KEY (`ID`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='" + tableDesc + "';\r\n";
                    continue;
                }
                createMysqlAppend = createMysqlAppend + "    `" + field.getField().toUpperCase() + "` " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + " COMMENT '" + field.getFieldName() + "',\r\n      PRIMARY KEY (`ID`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='" + tableDesc + "';\r\n";
                continue;
            }
            createMysqlAppend = "".equals(field.getFieldLength()) ? createMysqlAppend + "    `" + field.getField().toUpperCase() + "` " + field.getFieldType().toUpperCase() + field.getRequire() + " COMMENT '" + field.getFieldName() + "',\r\n " : createMysqlAppend + "    `" + field.getField().toUpperCase() + "` " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + " COMMENT '" + field.getFieldName() + "',\r\n ";
        }
        if (!StringUtils.isEmpty(kbCodeGenerateConfigTable.getForeignField()) && !"ID".equals(kbCodeGenerateConfigTable.getForeignField().toUpperCase())) {
            String fkName = "";
            fkName = tableName.contains("_") ? "IDX_" + tableName.substring(tableName.indexOf("_") + 1) + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() : "IDX_" + tableName + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase();
            createMysqlAppend = createMysqlAppend + "CREATE INDEX " + fkName.toUpperCase() + " ON " + tableName.toUpperCase() + " (" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() + ");\r\n";
        }
        createMysqlStr = createMysqlStr + "\r\n" + createMysqlAppend + "\r\n";
        return createMysqlStr;
    }

    public String createMysqlI118nSql(CoreCodeGenerateConfigBean coreCodeGenerateConfigBean, CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        String i18nStr = "";
        i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + ".SERVICE_NAME', '" + tableDesc + "', '" + tableDesc + "', 'zh-CN', '" + tableDesc + "');\r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = fieldList.get(j);
            i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + "." + field.getField().toUpperCase() + "', '" + tableDesc + "\u7684" + field.getFieldName() + "', '" + field.getFieldName() + "', 'zh-CN', '" + field.getFieldName() + "');\r\n";
        }
        return i18nStr;
    }

    public String createPgsqlSql(CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String createPgsqlStr = "";
        String createPgsqlAppend = "CREATE TABLE IF NOT EXISTS ";
        String tableName = kbCodeGenerateConfigTable.getTableName();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        ArrayList<CoreCodeGenerateConfigFieldBean> commentFields = new ArrayList<CoreCodeGenerateConfigFieldBean>();
        createPgsqlAppend = createPgsqlAppend + "" + tableName + "(\r\n ";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = new CoreCodeGenerateConfigFieldBean();
            BeanUtils.copyProperties(fieldList.get(j), field);
            if ("1".equals(field.getRequire())) {
                field.setRequire(" NULL");
            } else {
                field.setRequire(" NOT NULL");
            }
            if ("".equals(field.getFieldLength()) || field.getFieldLength() == null || "0".equals(field.getFieldLength())) {
                field.setFieldLength("");
            }
            if (Arrays.asList("date", "datetime").contains(field.getFieldType())) {
                field.setFieldType("timestamp");
            }
            if ("clob".equals(field.getFieldType())) {
                field.setFieldType("text");
            }
            if ("number".equals(field.getFieldType())) {
                if ("id".equals(field.getField())) {
                    field.setFieldType("bigint");
                } else if (field.getFieldLength().indexOf(",") > 0) {
                    field.setFieldType("decimal");
                } else {
                    field.setFieldType("bigint");
                }
            }
            createPgsqlAppend = j == fieldList.size() - 1 ? ("".equals(field.getFieldLength()) ? createPgsqlAppend + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + field.getRequire() + ",\r\n      PRIMARY KEY (ID)\r\n);\r\n" : createPgsqlAppend + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + ",\r\n      PRIMARY KEY (ID)\r\n);\r\n") : ("".equals(field.getFieldLength()) ? createPgsqlAppend + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + field.getRequire() + ",\r\n " : createPgsqlAppend + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + " ,\r\n ");
            commentFields.add(field);
        }
        String tableComment = String.format("comment on table %s is '%s';\r\n", tableName, tableDesc);
        StringBuilder commentStr = new StringBuilder(tableComment);
        for (CoreCodeGenerateConfigFieldBean commentField : commentFields) {
            commentStr.append(String.format("comment on column %s.%s is '%s';\r\n", tableName, commentField.getField(), commentField.getFieldName()));
        }
        createPgsqlAppend = createPgsqlAppend + commentStr;
        if (!StringUtils.isEmpty(kbCodeGenerateConfigTable.getForeignField()) && !"ID".equals(kbCodeGenerateConfigTable.getForeignField().toUpperCase())) {
            String fkName = "";
            fkName = tableName.contains("_") ? "IDX_" + tableName.substring(tableName.indexOf("_") + 1) + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() : "IDX_" + tableName + "_" + kbCodeGenerateConfigTable.getForeignField().toUpperCase();
            createPgsqlAppend = createPgsqlAppend + "CREATE INDEX " + fkName.toUpperCase() + " ON " + tableName.toUpperCase() + " (" + kbCodeGenerateConfigTable.getForeignField().toUpperCase() + ");\r\n";
        }
        createPgsqlStr = createPgsqlStr + "\r\n" + createPgsqlAppend + "\r\n";
        return createPgsqlStr;
    }

    public String createPgsqlI118nSql(CoreCodeGenerateConfigBean coreCodeGenerateConfigBean, CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        String i18nStr = "";
        i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + ".SERVICE_NAME', '" + tableDesc + "', '" + tableDesc + "', 'zh-CN', '" + tableDesc + "');\r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = fieldList.get(j);
            i18nStr = i18nStr + "CALL INSERTI18N('" + tableName.toUpperCase() + "." + field.getField().toUpperCase() + "', '" + tableDesc + "\u7684" + field.getFieldName() + "', '" + field.getFieldName() + "', 'zh-CN', '" + field.getFieldName() + "');\r\n";
        }
        return i18nStr;
    }

    public String createSqlServer(CoreCodeGenerateConfigTableBean sysCode, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String createSqlServerStr = "CREATE TABlE DBO.";
        String tableName = sysCode.getTableName();
        String tableDesc = sysCode.getTableDesc();
        String pkName = "";
        pkName = tableName.contains("_") ? "PK_" + tableName.substring(tableName.indexOf("_") + 1) : "PK_" + tableName;
        ArrayList<CoreCodeGenerateConfigFieldBean> newList = new ArrayList<CoreCodeGenerateConfigFieldBean>();
        createSqlServerStr = createSqlServerStr + tableName + "(\r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = new CoreCodeGenerateConfigFieldBean();
            BeanUtils.copyProperties(fieldList.get(j), field);
            if ("1".equals(field.getRequire())) {
                field.setRequire(" NULL ");
            } else {
                field.setRequire(" NOT NULL");
            }
            if ("".equals(field.getFieldLength()) || field.getFieldLength() == null || "0".equals(field.getFieldLength())) {
                field.setFieldLength("");
            }
            if ("date".equals(field.getFieldType())) {
                field.setFieldType("datetime");
            }
            if ("clob".equals(field.getFieldType())) {
                field.setFieldType("text");
            }
            if ("number".equals(field.getFieldType())) {
                if ("id".equals(field.getField())) {
                    field.setFieldType("bigint");
                } else if (field.getFieldLength().indexOf(",") > 0) {
                    field.setFieldType("decimal");
                } else {
                    field.setFieldType("bigint");
                    field.setFieldLength("");
                }
            }
            createSqlServerStr = j == fieldList.size() - 1 ? ("".equals(field.getFieldLength()) ? createSqlServerStr + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + field.getRequire() + ",\r\n    CONSTRAINT " + pkName + " PRIMARY KEY(ID));\r\n" : createSqlServerStr + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + ",\r\n    CONSTRAINT " + pkName + " PRIMARY KEY(ID));\r\n") : ("".equals(field.getFieldLength()) ? createSqlServerStr + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + field.getRequire() + ",\r\n" : createSqlServerStr + "    " + field.getField().toUpperCase() + " " + field.getFieldType().toUpperCase() + "(" + field.getFieldLength() + ") " + field.getRequire() + ",\r\n");
            newList.add(field);
        }
        createSqlServerStr = createSqlServerStr + "EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'" + tableDesc + "' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'" + tableName + "';\r\n";
        for (int a = 0; a < newList.size(); ++a) {
            createSqlServerStr = createSqlServerStr + "EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'" + ((CoreCodeGenerateConfigFieldBean)newList.get(a)).getFieldName() + "' , @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE',@level1name=N'" + tableName + "', @level2type=N'COLUMN',@level2name=N'" + ((CoreCodeGenerateConfigFieldBean)newList.get(a)).getField().toUpperCase() + "';\r\n";
        }
        if (!StringUtils.isEmpty(sysCode.getForeignField()) && !"ID".equals(sysCode.getForeignField().toUpperCase())) {
            String fkName = "";
            fkName = tableName.contains("_") ? "IDX_" + tableName.substring(tableName.indexOf("_") + 1) + "_" + sysCode.getForeignField().toUpperCase() : "IDX_" + tableName + "_" + sysCode.getForeignField().toUpperCase();
            createSqlServerStr = createSqlServerStr + "CREATE NONCLUSTERED INDEX [" + fkName.toUpperCase() + "] ON [dbo].[" + tableName.toUpperCase() + "] ([" + sysCode.getForeignField().toUpperCase() + "]);\r\n";
        }
        return createSqlServerStr + "\r\n";
    }

    public String createSqlserverI118nSql(CoreCodeGenerateConfigBean coreCodeGenerateConfigBean, CoreCodeGenerateConfigTableBean kbCodeGenerateConfigTable, List<CoreCodeGenerateConfigFieldBean> fieldList) {
        String tableName = kbCodeGenerateConfigTable.getTableName().toUpperCase();
        String tableDesc = kbCodeGenerateConfigTable.getTableDesc();
        String i18nStr = "";
        i18nStr = i18nStr + "EXEC  DBO.INSERTI18N  N'" + tableName.toUpperCase() + ".SERVICE_NAME', N'" + tableDesc + "', N'" + tableDesc + "', N'zh-CN',N'" + tableDesc + "';\r\n";
        for (int j = 0; j < fieldList.size(); ++j) {
            CoreCodeGenerateConfigFieldBean field = fieldList.get(j);
            i18nStr = i18nStr + "EXEC  DBO.INSERTI18N  N'" + tableName.toUpperCase() + "." + field.getField().toUpperCase() + "', N'" + tableDesc + "\u7684" + field.getFieldName() + "', N'" + field.getFieldName() + "', N'zh-CN',N'" + field.getFieldName() + "';\r\n";
        }
        return i18nStr;
    }

    private String addWarnInfo(String identifier, String sql) {
        boolean warnTableValidate = DbMetaCheckHelper.validateWarn(identifier);
        if (!warnTableValidate) {
            sql = sql + "\n -- WARNING NAME " + identifier + " TOO LONG FOR ORACLE VERSION < 12.2 \n";
        }
        return sql;
    }
}
