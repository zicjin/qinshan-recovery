/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.helper.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.FieldType;
import com.sunwayworld.cloud.module.lcdp.table.helper.IndexType;
import com.sunwayworld.cloud.module.lcdp.table.helper.SqlGenerateHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value={"sqlserver"})
public class SqlserverSqlGenereateHelper
implements SqlGenerateHelper {
    @Override
    public String generateTableCommentSql(LcdpTableBean table, LcdpTableDTO physicalTable) {
        if (StringUtils.isEmpty(physicalTable.getTableDesc())) {
            return MessageFormat.format("EXEC sys.sp_addextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}''", this.nullToEmpty(table.getTableDesc()), table.getTableName());
        }
        return MessageFormat.format("EXEC sys.sp_updateextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}''", this.nullToEmpty(table.getTableDesc()), table.getTableName());
    }

    @Override
    public String generateIndexSql(LcdpTableBean table, LcdpTableIndexBean index) {
        String tableName = table.getTableName();
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE {0} DROP CONSTRAINT {1}", tableName, index.getIndexName());
        }
        if (!IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "delete".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("DROP INDEX {0} ON {1}", index.getIndexName(), tableName);
        }
        if (IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("ALTER TABLE {0} ADD CONSTRAINT {1} PRIMARY KEY ({2})", tableName, index.getIndexName(), index.getIndexField());
        }
        if (IndexType.unique.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE UNIQUE INDEX {0} ON {1}({2})", index.getIndexName(), tableName, index.getIndexField());
        }
        if (IndexType.regular.name().equalsIgnoreCase(index.getIndexType()) && "add".equalsIgnoreCase(index.getIndexOperationType())) {
            return MessageFormat.format("CREATE NONCLUSTERED INDEX {0} ON {1}({2})", index.getIndexName(), tableName, index.getIndexField());
        }
        return "";
    }

    @Override
    public List<String> generateColumnSql(LcdpTableBean table, LcdpTableFieldBean oldField, LcdpTableFieldBean field) {
        String tableName = table.getTableName();
        ArrayList<String> sqlList = new ArrayList<String>();
        if ("update".equalsIgnoreCase(field.getFieldOperationType())) {
            if (!StringUtils.equals(this.parseFieldType(oldField), this.parseFieldType(field)) || !StringUtils.equals(oldField.getAllowNull(), field.getAllowNull())) {
                sqlList.add(MessageFormat.format("ALTER TABLE {0} ALTER COLUMN {1} {2} {3}", tableName, field.getFieldName(), this.parseFieldType(field), this.parseIsNULL(field)));
            }
            if (!StringUtils.equals(this.nullToEmpty(oldField.getDefaultValue()), this.nullToEmpty(field.getDefaultValue()))) {
                if (!StringUtils.isEmpty(oldField.getDefaultValue())) {
                    sqlList.add(MessageFormat.format("EXEC dbo.p_dropcolumnconstraint @tableName=N''{0}'', @columnName=N''{1}''", tableName, field.getFieldName()));
                }
                if (!StringUtils.isEmpty(field.getDefaultValue())) {
                    sqlList.add(MessageFormat.format("ALTER TABLE {0} ADD DEFAULT ({1}) FOR {2} WITH VALUES", tableName, field.getDefaultValue(), field.getFieldName()));
                }
            }
            if (!StringUtils.equals(this.nullToEmpty(oldField.getFieldComment()), this.nullToEmpty(field.getFieldComment()))) {
                if (StringUtils.isEmpty(oldField.getFieldComment())) {
                    sqlList.add(MessageFormat.format("EXEC sys.sp_addextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}'', @level2type=N''COLUMN'',@level2name=N''{2}''", this.nullToEmpty(field.getFieldComment()), tableName, field.getFieldName()));
                } else {
                    sqlList.add(MessageFormat.format("EXEC sys.sp_updateextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}'', @level2type=N''COLUMN'',@level2name=N''{2}''", this.nullToEmpty(field.getFieldComment()), tableName, field.getFieldName()));
                }
            }
        } else if ("delete".equalsIgnoreCase(field.getFieldOperationType())) {
            if (!StringUtils.isEmpty(oldField.getDefaultValue())) {
                sqlList.add(MessageFormat.format("EXEC dbo.p_dropcolumnconstraint @tableName=N''{0}'', @columnName=N''{1}''", tableName, field.getFieldName()));
            }
            sqlList.add(MessageFormat.format("ALTER TABLE {0} DROP COLUMN {1}", tableName, field.getFieldName()));
        } else if ("add".equalsIgnoreCase(field.getFieldOperationType())) {
            sqlList.add(MessageFormat.format("ALTER TABLE {0} ADD {1} {2} {3} {4}", tableName, field.getFieldName(), this.parseFieldType(field), this.parseDefaultValue(field), this.parseIsNULL(field)));
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("EXEC sys.sp_addextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}'', @level2type=N''COLUMN'',@level2name=N''{2}''", this.nullToEmpty(field.getFieldComment()), tableName, field.getFieldName()));
            }
        }
        return sqlList;
    }

    @Override
    public List<String> generateCreateTableSql(LcdpTableBean table, List<LcdpTableFieldBean> fields, LcdpTableIndexBean primaryKey) {
        String tableName = table.getTableName();
        String tableDesc = table.getTableDesc();
        ArrayList<String> sqlList = new ArrayList<String>();
        StringBuilder createSqlAppend = new StringBuilder(MessageFormat.format("CREATE TABLE {0} (\n", tableName));
        for (int j = 0; j < fields.size(); ++j) {
            LcdpTableFieldBean field2 = fields.get(j);
            createSqlAppend.append(MessageFormat.format(" {0} {1} {2} {3} ", field2.getFieldName(), this.parseFieldType(field2), this.parseDefaultValue(field2), this.parseIsNULL(field2)));
            if (j == fields.size() - 1) {
                if (!ObjectUtils.isEmpty(primaryKey)) {
                    createSqlAppend.append(",\n");
                    createSqlAppend.append(MessageFormat.format("CONSTRAINT {0} PRIMARY KEY ({1})\n", primaryKey.getIndexName(), primaryKey.getIndexField()));
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
            sqlList.add(MessageFormat.format("EXEC sys.sp_addextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}''", this.nullToEmpty(table.getTableDesc()), table.getTableName()));
        }
        Optional.of(fields).orElse(Collections.emptyList()).stream().filter(field -> "add".equalsIgnoreCase(field.getFieldOperationType())).forEach(field -> {
            if (!StringUtils.isEmpty(field.getFieldComment())) {
                sqlList.add(MessageFormat.format("EXEC sys.sp_addextendedproperty @name=N''MS_Description'', @value=N''{0}'',@level0type=N''SCHEMA'', @level0name=N''dbo'', @level1type=N''TABLE'',@level1name=N''{1}'', @level2type=N''COLUMN'',@level2name=N''{2}''", this.nullToEmpty(field.getFieldComment()), tableName, field.getFieldName()));
            }
        });
        return sqlList;
    }

    @Override
    public String databaseTypeToFieldType(String databaseType) {
        switch (databaseType.toUpperCase()) {
            case "VARCHAR":
            case "NVARCHAR": {
                return FieldType.varchar.name();
            }
            case "DATETIME":
            case "DATETIME2": {
                return FieldType.date.name();
            }
            case "TEXT":
            case "LONGTEXT": {
                return FieldType.clob.name();
            }
            case "DECIMAL":
            case "BIGINT":
            case "NUMERIC":
            case "INT": {
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
            fieldType = "TEXT";
        } else if (FieldType.number.name().equalsIgnoreCase(field.getFieldType())) {
            if (field.getPrecision() > 0 && field.getScale() > 0) {
                field.setFieldLength(field.getPrecision() + "," + field.getScale());
                fieldLength = "(" + field.getPrecision() + "," + field.getScale() + ")";
                fieldType = "DECIMAL" + fieldLength;
            } else if (field.getPrecision() > 0 && field.getScale() <= 0) {
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
    public String generateAlterViewSql(LcdpViewBean view) {
        return MessageFormat.format("ALTER VIEW {0} AS {1}", view.getTableName(), view.getSelectStatement());
    }

    @Override
    public String parseSQLException(String message) {
        if (StringUtils.containsIgnoreCase(message, "permission denied")) {
            return I18nHelper.getMessage("LCDP.MODULE.DATABASE.TIP.INSUFFICIENT_PRIVILEGES", new String[0]);
        }
        return message;
    }
}
