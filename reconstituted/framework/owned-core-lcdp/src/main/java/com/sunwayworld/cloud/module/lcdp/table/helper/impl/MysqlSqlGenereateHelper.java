/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.helper.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.FieldType;
import com.sunwayworld.cloud.module.lcdp.table.helper.IndexType;
import com.sunwayworld.cloud.module.lcdp.table.helper.SqlGenerateHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value={"mysql"})
public class MysqlSqlGenereateHelper
implements SqlGenerateHelper {
    @Override
    public String generateTableCommentSql(LcdpTableBean table, LcdpTableDTO physicalTable) {
        return MessageFormat.format("ALTER TABLE `{0}` COMMENT ''{1}''", table.getTableName(), this.nullToEmpty(table.getTableDesc()));
    }

    @Override
    public String generateIndexSql(LcdpTableBean table, LcdpTableIndexBean index) {
        String tableName = table.getTableName();
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE `{0}` DROP PRIMARY KEY", tableName);
        }
        if (!IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE `{0}` DROP INDEX `{1}`", tableName, index.getIndexName());
        }
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE `{0}` ADD  PRIMARY KEY ({1})", tableName, this.parseIndexField(index));
        }
        if (IndexType.unique.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE UNIQUE INDEX `{0}` ON `{1}`({2})", index.getIndexName(), tableName, this.parseIndexField(index));
        }
        if (IndexType.regular.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE INDEX `{0}` ON `{1}`({2})", index.getIndexName(), tableName, this.parseIndexField(index));
        }
        return "";
    }

    @Override
    public List<String> generateColumnSql(LcdpTableBean table, LcdpTableFieldBean oldField, LcdpTableFieldBean field) {
        String tableName = table.getTableName();
        ArrayList<String> sqlList = new ArrayList<String>();
        if ("update".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE `{0}` MODIFY `{1}` {2} {3} {4} COMMENT ''{5}''", tableName, field.getFieldName(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field), this.nullToEmpty(field.getFieldComment())));
        } else if ("delete".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE `{0}` DROP `{1}`", tableName, field.getFieldName()));
        } else if ("add".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE `{0}` ADD `{1}` {2} {3} {4} COMMENT ''{5}''", tableName, field.getFieldName(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field), this.nullToEmpty(field.getFieldComment())));
        }
        return sqlList;
    }

    @Override
    public List<String> generateCreateTableSql(LcdpTableBean table, List<LcdpTableFieldBean> fields, LcdpTableIndexBean primaryKey) {
        String tableName = table.getTableName();
        String tableDesc = table.getTableDesc();
        ArrayList<String> sqlList = new ArrayList<String>();
        StringBuilder createSqlAppend = new StringBuilder(MessageFormat.format("CREATE TABLE `{0}` (\n", tableName));
        for (int j = 0; j < fields.size(); ++j) {
            LcdpTableFieldBean field = fields.get(j);
            createSqlAppend.append(MessageFormat.format(" `{0}` {1} {2} {3} COMMENT ''{4}''", field.getFieldName(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field), this.nullToEmpty(field.getFieldComment())));
            if (j == fields.size() - 1) {
                if (!ObjectUtils.isEmpty(primaryKey)) {
                    createSqlAppend.append(",\n");
                    createSqlAppend.append(MessageFormat.format("PRIMARY KEY ({0})\n", this.parseIndexField(primaryKey)));
                    continue;
                }
                createSqlAppend.append("\n");
                continue;
            }
            createSqlAppend.append(",\n");
        }
        createSqlAppend.append(MessageFormat.format(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT=''{0}''", this.nullToEmpty(tableDesc)));
        sqlList.add(createSqlAppend.toString());
        return sqlList;
    }

    @Override
    public String databaseTypeToFieldType(String databaseType) {
        switch (databaseType.toUpperCase()) {
            case "VARCHAR":
            case "CHAR":
            case "BIT": {
                return FieldType.varchar.name();
            }
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
            case "YEAR":
            case "TIME": {
                return FieldType.date.name();
            }
            case "TEXT":
            case "LONGTEXT":
            case "LONGBLOB":
            case "MEDIUMTEXT": {
                return FieldType.clob.name();
            }
            case "DECIMAL":
            case "BIGINT":
            case "INT":
            case "DOUBLE":
            case "FLOAT": {
                return FieldType.number.name();
            }
        }
        return databaseType;
    }

    @Override
    public String parseFieldType(LcdpTableFieldBean field) {
        String fieldType = "";
        String fieldLength = "";
        if (FieldType.varchar.name().equalsIgnoreCase(field.getFieldType())) {
            if (!StringUtils.isEmpty(field.getFieldLength())) {
                fieldLength = "(" + field.getFieldLength() + ")";
            }
            fieldType = "VARCHAR" + fieldLength;
        } else if (FieldType.date.name().equalsIgnoreCase(field.getFieldType())) {
            fieldType = "DATETIME";
        } else if (FieldType.clob.name().equalsIgnoreCase(field.getFieldType())) {
            fieldType = "LONGTEXT";
        } else if (FieldType.number.name().equalsIgnoreCase(field.getFieldType())) {
            if (field.getPrecision() > 0 && field.getScale() == null) {
                field.setFieldLength(field.getPrecision() + "," + field.getPrecision() / 2);
                fieldLength = "(" + field.getPrecision() + "," + field.getPrecision() / 2 + ")";
                fieldType = "DECIMAL" + fieldLength;
            } else if (field.getPrecision() > 0 && field.getScale() > 0) {
                field.setFieldLength(field.getPrecision() + "," + field.getScale());
                fieldLength = "(" + field.getPrecision() + "," + field.getScale() + ")";
                fieldType = "DECIMAL" + fieldLength;
            } else if (field.getPrecision() > 0 && field.getScale() <= 0) {
                field.setFieldLength(String.valueOf(field.getPrecision()));
                fieldLength = "(" + field.getPrecision() + ")";
                fieldType = "BIGINT" + fieldLength;
            } else {
                fieldType = "BIGINT";
            }
        }
        return fieldType.toUpperCase();
    }

    @Override
    public String getReservedIdentifier() {
        return "`";
    }

    @Override
    public String parseSQLException(String message) {
        if (StringUtils.containsIgnoreCase(message, "ERROR 1142 (42000)")) {
            return I18nHelper.getMessage("LCDP.MODULE.DATABASE.TIP.INSUFFICIENT_PRIVILEGES", new String[0]);
        }
        return message;
    }
}
