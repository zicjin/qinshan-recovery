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
import com.sunwayworld.cloud.module.lcdp.table.helper.impl.LcdpOracleCondition;
import com.sunwayworld.framework.database.dialect.DialectRepository;
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
@Conditional(value={LcdpOracleCondition.class})
public class OracleSqlGenereateHelper
implements SqlGenerateHelper {
    @Override
    public String generateTableCommentSql(LcdpTableBean table, LcdpTableDTO physicalTable) {
        return MessageFormat.format("COMMENT ON TABLE \"{0}\" IS ''{1}''", table.getTableName(), this.nullToEmpty(table.getTableDesc()));
    }

    @Override
    public String generateIndexSql(LcdpTableBean table, LcdpTableIndexBean index) {
        String tableName = table.getTableName();
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE \"{0}\" DROP CONSTRAINT \"{1}\"", tableName, index.getIndexName());
        }
        if (!IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("DROP INDEX \"{0}\"", index.getIndexName());
        }
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            String primaryKey = this.parseIndexField(index);
            String primaryName = index.getIndexName();
            if (StringUtils.equalsIgnoreCase("primary", primaryName)) {
                primaryName = "PK_" + tableName;
            }
            return MessageFormat.format("ALTER TABLE \"{0}\" ADD CONSTRAINT \"{1}\" PRIMARY KEY ({2})", tableName, primaryName, primaryKey);
        }
        if (IndexType.unique.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE UNIQUE INDEX \"{0}\" ON \"{1}\"({2})", index.getIndexName(), tableName, this.parseIndexField(index));
        }
        if (IndexType.regular.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE INDEX \"{0}\" ON \"{1}\"({2})", index.getIndexName(), tableName, this.parseIndexField(index));
        }
        return "";
    }

    @Override
    public List<String> generateColumnSql(LcdpTableBean table, LcdpTableFieldBean currentField, LcdpTableFieldBean field) {
        String tableName = table.getTableName();
        ArrayList<String> sqlList = new ArrayList<String>();
        if ("update".equalsIgnoreCase(field.getFieldOperationType())) {
            if (!StringUtils.equals(this.parseFieldType(currentField), this.parseFieldType(field)) || !StringUtils.equals(currentField.getAllowNull(), field.getAllowNull())) {
                sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" MODIFY \"{1}\" {2} {3}", tableName, field.getFieldName(), StringUtils.equals(this.parseFieldType(currentField), this.parseFieldType(field)) ? "" : this.parseFieldType(field), StringUtils.equals(currentField.getAllowNull(), field.getAllowNull()) ? "" : this.parseIsNULL(field)));
            }
            if (!StringUtils.equals(this.nullToEmpty(currentField.getDefaultValue()), this.nullToEmpty(field.getDefaultValue()))) {
                if (StringUtils.isEmpty(field.getDefaultValue())) {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" MODIFY \"{1}\" DEFAULT NULL", tableName, field.getFieldName()));
                } else {
                    sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" MODIFY \"{1}\" {2}", tableName, field.getFieldName(), this.parseDefaultValue(field)));
                }
            }
            if (!StringUtils.equals(this.nullToEmpty(currentField.getFieldComment()), this.nullToEmpty(field.getFieldComment()))) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName(), this.nullToEmpty(field.getFieldComment())));
            }
        } else if ("delete".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" DROP COLUMN \"{1}\"", tableName, field.getFieldName()));
        } else if ("add".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE \"{0}\" ADD \"{1}\" {2} {3} {4}", tableName, field.getFieldName(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field)));
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName(), this.nullToEmpty(field.getFieldComment())));
            }
        }
        return sqlList;
    }

    @Override
    public List<String> generateCreateTableSql(LcdpTableBean table, List<LcdpTableFieldBean> fields, LcdpTableIndexBean primaryKey) {
        String tableName = table.getTableName();
        String tableDesc = table.getTableDesc();
        ArrayList<String> sqlList = new ArrayList<String>();
        StringBuilder createSqlAppend = new StringBuilder(MessageFormat.format("CREATE TABLE \"{0}\" (\n", tableName));
        for (int j = 0; j < fields.size(); ++j) {
            LcdpTableFieldBean field2 = fields.get(j);
            createSqlAppend.append(MessageFormat.format(" \"{0}\" {1} {2} {3} ", field2.getFieldName(), this.parseFieldType(field2), this.parseDefaultValue(field2), this.parseIsNULL(field2)));
            if (j == fields.size() - 1) {
                if (!ObjectUtils.isEmpty(primaryKey)) {
                    createSqlAppend.append(",\n");
                    String primaryName = primaryKey.getIndexName();
                    if (StringUtils.equalsIgnoreCase("primary", primaryName)) {
                        primaryName = "PK_" + tableName;
                    }
                    createSqlAppend.append(MessageFormat.format("CONSTRAINT \"{0}\" PRIMARY KEY ({1})\n", primaryName, this.parseIndexField(primaryKey)));
                    continue;
                }
                createSqlAppend.append("\n");
                continue;
            }
            createSqlAppend.append(",\n");
        }
        createSqlAppend.append(")");
        sqlList.add(createSqlAppend.toString());
        if (!StringUtils.isEmpty(tableDesc)) {
            sqlList.add(MessageFormat.format("COMMENT ON TABLE \"{0}\" IS ''{1}''", tableName, this.nullToEmpty(tableDesc)));
        }
        Optional.of(fields).orElse(Collections.emptyList()).stream().filter(field -> "add".equalsIgnoreCase(field.getFieldOperationType())).forEach(field -> {
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("COMMENT ON COLUMN \"{0}\".\"{1}\" IS ''{2}''", tableName, field.getFieldName(), this.nullToEmpty(field.getFieldComment())));
            }
        });
        return sqlList;
    }

    @Override
    public String databaseTypeToFieldType(String databaseType) {
        switch (databaseType.toUpperCase()) {
            case "VARCHAR2":
            case "VARCHAR":
            case "NVARCHAR2":
            case "UROWID":
            case "CHAR":
            case "RAW":
            case "BLOB": {
                return FieldType.varchar.name();
            }
            case "DATE":
            case "TIMESTAMP":
            case "DATETIME": {
                return FieldType.date.name();
            }
            case "CLOB":
            case "TEXT": {
                return FieldType.clob.name();
            }
            case "NUMBER":
            case "FLOAT":
            case "BIGINT":
            case "DECIMAL":
            case "INT": {
                return FieldType.number.name();
            }
        }
        if (StringUtils.startsWithIgnoreCase(databaseType, "TIMESTAMP")) {
            return FieldType.date.name();
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
            fieldType = "VARCHAR2" + fieldLength;
        } else if (FieldType.date.name().equalsIgnoreCase(field.getFieldType())) {
            fieldType = DialectRepository.isDameng() ? "DATETIME" : "DATE";
        } else if (FieldType.clob.name().equalsIgnoreCase(field.getFieldType())) {
            fieldType = "CLOB";
        } else if (FieldType.number.name().equalsIgnoreCase(field.getFieldType())) {
            Integer precision = field.getPrecision() == null ? 0 : field.getPrecision();
            Integer scale = field.getScale() == null ? 0 : field.getScale();
            if (precision > 0 && scale > 0) {
                field.setFieldLength(precision + "," + scale);
                fieldLength = "(" + precision + "," + scale + ")";
            } else if (precision > 0 && scale <= 0) {
                field.setFieldLength(String.valueOf(precision));
                fieldLength = "(" + precision + ")";
            }
            fieldType = "NUMBER" + fieldLength;
        }
        return fieldType.toUpperCase();
    }

    @Override
    public String parseSQLException(String message) {
        if (StringUtils.containsIgnoreCase(message, "ORA-01031")) {
            return I18nHelper.getMessage("LCDP.MODULE.DATABASE.TIP.INSUFFICIENT_PRIVILEGES", new String[0]);
        }
        return message;
    }
}
