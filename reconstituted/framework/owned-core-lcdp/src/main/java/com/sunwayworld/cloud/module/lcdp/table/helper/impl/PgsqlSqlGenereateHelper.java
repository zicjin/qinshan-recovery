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
import com.sunwayworld.cloud.module.lcdp.table.helper.impl.LcdpPgsqlCondition;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(value={LcdpPgsqlCondition.class})
public class PgsqlSqlGenereateHelper
implements SqlGenerateHelper {
    @Override
    public String generateTableCommentSql(LcdpTableBean table, LcdpTableDTO physicalTable) {
        return MessageFormat.format("COMMENT ON TABLE \"{0}\" IS ''{1}''", table.getTableName().toLowerCase(), this.nullToEmpty(table.getTableDesc()));
    }

    @Override
    public String generateIndexSql(LcdpTableBean table, LcdpTableIndexBean index) {
        String tableName = table.getTableName().toLowerCase();
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE \"{0}\" DROP CONSTRAINT \"{1}\"", tableName, index.getIndexName().toLowerCase());
        }
        if (!IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("DROP INDEX \"{0}\"", index.getIndexName());
        }
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE \"{0}\" ADD CONSTRAINT \"{1}\" PRIMARY KEY ({2})", tableName, index.getIndexName().toLowerCase(), this.parseIndexField(index).toLowerCase());
        }
        if (IndexType.unique.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE UNIQUE INDEX \"{0}\" ON \"{1}\"({2})", index.getIndexName().toLowerCase(), tableName, this.parseIndexField(index).toLowerCase());
        }
        if (IndexType.regular.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE INDEX \"{0}\" ON \"{1}\"({2})", index.getIndexName().toLowerCase(), tableName, this.parseIndexField(index).toLowerCase());
        }
        return "";
    }

    @Override
    public List<String> generateColumnSql(LcdpTableBean table, LcdpTableFieldBean oldField, LcdpTableFieldBean field) {
        String tableName = table.getTableName().toLowerCase();
        ArrayList<String> sqlList = new ArrayList<String>();
        if ("update".equalsIgnoreCase(field.getFieldOperationType())) {
            if (!StringUtils.equals(this.parseFieldType(oldField), this.parseFieldType(field))) {
                sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ALTER COLUMN \"{1}\" TYPE {2}", tableName, field.getFieldName().toLowerCase(), this.parseFieldType(field)));
            }
            if (!StringUtils.equals(this.nullToEmpty(oldField.getDefaultValue()), this.nullToEmpty(field.getDefaultValue()))) {
                if (!StringUtils.isEmpty(field.getDefaultValue())) {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ALTER COLUMN \"{1}\" SET {2}", tableName, field.getFieldName().toLowerCase(), this.parseDefaultValue(field)));
                } else {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ALTER COLUMN \"{1}\" DROP DEFAULT", tableName, field.getFieldName().toLowerCase()));
                }
            }
            if (!StringUtils.equals(oldField.getAllowNull(), field.getAllowNull())) {
                if ("1".equals(field.getAllowNull())) {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ALTER COLUMN \"{1}\" DROP NOT NULL", tableName, field.getFieldName().toLowerCase()));
                } else if ("0".equals(field.getAllowNull())) {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ALTER COLUMN \"{1}\" SET NOT NULL", tableName, field.getFieldName().toLowerCase()));
                }
            }
            if (!StringUtils.equals(this.nullToEmpty(oldField.getFieldComment()), this.nullToEmpty(field.getFieldComment()))) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName().toLowerCase(), this.nullToEmpty(field.getFieldComment())));
            }
        } else if ("delete".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" DROP COLUMN \"{1}\"", tableName, field.getFieldName().toLowerCase()));
        } else if ("add".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ADD \"{1}\" {2} {3} {4}", tableName, field.getFieldName().toLowerCase(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field)));
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName().toLowerCase(), this.nullToEmpty(field.getFieldComment())));
            }
        }
        return sqlList;
    }

    @Override
    public List<String> generateCreateTableSql(LcdpTableBean table, List<LcdpTableFieldBean> fields, LcdpTableIndexBean primaryKey) {
        String tableName = table.getTableName().toLowerCase();
        String tableDesc = table.getTableDesc();
        ArrayList<String> sqlList = new ArrayList<String>();
        StringBuilder createSqlAppend = new StringBuilder(MessageFormat.format("CREATE TABLE \"{0}\" (\n", tableName));
        for (int j = 0; j < fields.size(); ++j) {
            LcdpTableFieldBean field2 = fields.get(j);
            createSqlAppend.append(MessageFormat.format(" \"{0}\" {1} {2} {3} ", field2.getFieldName().toLowerCase(), this.parseFieldType(field2), this.parseDefaultValue(field2), this.parseIsNULL(field2), this.nullToEmpty(field2.getFieldComment())));
            if (j == fields.size() - 1) {
                if (!ObjectUtils.isEmpty(primaryKey)) {
                    createSqlAppend.append(",\n");
                    createSqlAppend.append(MessageFormat.format("PRIMARY KEY ({0})\n", this.parseIndexField(primaryKey).toLowerCase()));
                    continue;
                }
                createSqlAppend.append("\n");
                continue;
            }
            createSqlAppend.append(",\n");
        }
        createSqlAppend.append(MessageFormat.format(")", this.nullToEmpty(tableDesc)));
        sqlList.add(createSqlAppend.toString());
        if (!StringUtils.isEmpty(tableDesc)) {
            sqlList.add(MessageFormat.format("COMMENT ON TABLE \"{0}\" IS ''{1}''", tableName, this.nullToEmpty(tableDesc)));
        }
        Optional.of(fields).orElse(Collections.emptyList()).stream().filter(field -> "add".equalsIgnoreCase(field.getFieldOperationType())).forEach(field -> {
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName().toLowerCase(), this.nullToEmpty(field.getFieldComment())));
            }
        });
        return sqlList;
    }

    @Override
    public String databaseTypeToFieldType(String databaseType) {
        switch (databaseType.toUpperCase()) {
            case "VARCHAR": {
                return FieldType.varchar.name();
            }
            case "DATE":
            case "TIMESTAMP": {
                return FieldType.date.name();
            }
            case "TEXT":
            case "LONGTEXT": {
                return FieldType.clob.name();
            }
            case "DECIMAL":
            case "INT8":
            case "INT4":
            case "NUMERIC": {
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
            fieldType = "DATE";
        } else if (FieldType.clob.name().equalsIgnoreCase(field.getFieldType())) {
            fieldType = "TEXT";
        } else if (FieldType.number.name().equalsIgnoreCase(field.getFieldType())) {
            if (!ObjectUtils.isEmpty(field.getPrecision()) && !ObjectUtils.isEmpty(field.getScale()) && field.getPrecision() > 0 && field.getScale() > 0) {
                field.setFieldLength(field.getPrecision() + "," + field.getScale());
                fieldLength = "(" + field.getPrecision() + "," + field.getScale() + ")";
                fieldType = "DECIMAL" + fieldLength;
            } else if (!ObjectUtils.isEmpty(field.getPrecision()) && field.getPrecision() > 0 && (ObjectUtils.isEmpty(field.getScale()) || field.getScale() <= 0)) {
                field.setFieldLength(String.valueOf(field.getPrecision()));
                fieldLength = "(" + field.getPrecision() + ")";
                fieldType = "NUMERIC" + fieldLength;
            } else {
                fieldType = "BIGINT";
            }
        }
        return fieldType.toUpperCase();
    }

    @Override
    public String parseSQLException(String message) {
        if (StringUtils.containsIgnoreCase(message, "permission denied")) {
            return I18nHelper.getMessage("LCDP.MODULE.DATABASE.TIP.INSUFFICIENT_PRIVILEGES", new String[0]);
        }
        return message;
    }
}
