/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.ColumnContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityColumnContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.dialect.ColumnMetadata;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.exception.database.UnsupportedDataTypeJdbcException;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SqlserverDialect
implements Dialect {
    @Override
    public String getDatabase() {
        return "sqlserver";
    }

    @Override
    public String getSelectFromTableSql(String table) {
        return "SELECT * FROM " + table;
    }

    @Override
    public String getLimitSql(String sql, int offset, int limit) {
        if (limit == 0) {
            return "SELECT TOP 0 T.* FROM (" + SqlHelper.removeEndingOrderIfPresent(sql.trim()) + ") T";
        }
        if (SqlHelper.hasOrderSegment(sql)) {
            return sql + "\n OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }
        return sql;
    }

    @Override
    public String getSelectOptionsSql(String sql, String property) {
        if (SqlHelper.isRiskySqlSegment(property)) {
            throw new HackingDataException(property);
        }
        return "SELECT DISTINCT " + property + " FROM (\n" + SqlHelper.removeEndingOrderIfPresent(sql) + "\n) S_ ORDER BY " + property;
    }

    @Override
    public Class<?> getJavaType(String columnName, String dataType, int scale) {
        switch (dataType) {
            case "varchar":
            case "text":
            case "varbinary":
            case "nvarchar":
            case "varbinary(max)":
            case "char":
            case "nchar":
            case "ntext":
            case "sql_variant": {
                return String.class;
            }
            case "int":
            case "bigint":
            case "tinyint": {
                return Long.class;
            }
            case "float": {
                return Double.class;
            }
            case "numeric":
            case "decimal": {
                if (scale > 0) {
                    return Double.class;
                }
                return Long.class;
            }
            case "date": {
                return LocalDate.class;
            }
            case "datetime":
            case "datetime2":
            case "timestamp": {
                return LocalDateTime.class;
            }
        }
        throw new UnsupportedDataTypeJdbcException(columnName + "@" + dataType + "@" + scale);
    }

    @Override
    public boolean isClob(ColumnContext columnContext) {
        switch (columnContext.getDataType().toLowerCase()) {
            case "text": {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBlob(ColumnContext columnContext) {
        switch (columnContext.getDataType().toLowerCase()) {
            case "varbinary(max)": {
                return true;
            }
            case "varbinary": {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getNullReplacementClause(String column, String valueOrColumn) {
        return "ISNULL( " + column + ", " + valueOrColumn + ")";
    }

    @Override
    public String getSumColumnSql(String column, String valueOrColumn) {
        return "SUM(" + this.getNullReplacementClause(column, valueOrColumn) + ")";
    }

    @Override
    public String getConcatClause(String ... columnOrValues) {
        StringBuilder sb = new StringBuilder("(");
        Arrays.stream(columnOrValues).forEach(v -> sb.append("ISNULL(").append(ConvertUtils.convert(v, String.class, "")).append(", '') + "));
        return sb.deleteCharAt(sb.length() - 2).append(")").toString();
    }

    @Override
    public String getSelectFirstSql(String sql) {
        return "SELECT TOP 1 " + StringUtils.removeStartIgnoreCase(sql.trim(), "SELECT ");
    }

    @Override
    public String getSelectTodoSql(String todoSql) {
        return "SELECT T.MENUID, T.MODELNAME, T.HREF, COUNT(1) QTY\n" + "  FROM (" + todoSql + ") T\n" + " GROUP BY T.MENUID, T.MODELNAME, T.HREF";
    }

    @Override
    public String getSelectNoResultSql() {
        return "SELECT 1 FROM (SELECT 1 AS ID) T WHERE T.ID = 0";
    }

    @Override
    public int getInClauseIdMaxQty() {
        return 2098;
    }

    @Override
    public int getMaxParamQty() {
        return 2098;
    }

    @Override
    public List<String> getAddColumnSqlList(String tableName, ColumnMetadata columnMetadata) {
        if (!StringUtils.startsWithIgnoreCase(columnMetadata.getColumnName(), "EXT_")) {
            throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
        }
        if (SqlHelper.isRiskySqlSegment(columnMetadata.getColumnName())) {
            throw new HackingDataException(columnMetadata.getColumnName());
        }
        StringBuilder addSql = new StringBuilder("ALTER TABLE ").append(tableName).append(" ADD ").append(columnMetadata.getColumnName()).append(" ");
        switch (columnMetadata.getDataType()) {
            case STRING: {
                addSql.append("VARCHAR(").append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(96)).append(") ");
                break;
            }
            case INTEGER: {
                addSql.append("BIGINT ");
                break;
            }
            case DOUBLE:
            case FLOAT: {
                addSql.append("DECIMAL(").append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(16)).append(", ").append(Optional.ofNullable(columnMetadata.getDataScale()).orElse(6)).append(") ");
                break;
            }
            case CLOB: {
                addSql.append("TEXT ");
                break;
            }
            case DATE: {
                addSql.append("DATE ");
                break;
            }
            case DATETIME: {
                addSql.append("DATETIME ");
                break;
            }
        }
        if (columnMetadata.isNullable()) {
            addSql.append(" NULL ");
        } else {
            addSql.append(" NOT NULL ");
        }
        StringBuilder commentSql = new StringBuilder("EXEC sp_addextendedproperty 'MS_Description', N'").append(StringUtils.replace(columnMetadata.getComments(), "'", "")).append("', 'SCHEMA', N'dbo', 'TABLE', N'").append(tableName).append("', 'COLUMN', N'").append(columnMetadata.getColumnName()).append("'");
        return Arrays.asList(addSql.toString(), commentSql.toString());
    }

    @Override
    public String getUpdateVarcharLengthSql(String tableName, String columnName, int length) {
        if (!StringUtils.startsWithIgnoreCase(columnName, "EXT_")) {
            throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
        }
        if (SqlHelper.isRiskySqlSegment(tableName)) {
            throw new HackingDataException(tableName);
        }
        if (SqlHelper.isRiskySqlSegment(columnName)) {
            throw new HackingDataException(columnName);
        }
        return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " VARCHAR(" + length + ")";
    }

    @Override
    public String getDropColumnSql(String tableName, String columnName) {
        if (!StringUtils.startsWithIgnoreCase(columnName, "EXT_")) {
            throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
        }
        if (SqlHelper.isRiskySqlSegment(columnName)) {
            throw new HackingDataException(columnName);
        }
        return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
    }

    @Override
    public GlobalBoundSql getBatchInsertSql(EntityContext context, List<EntityContextInstance> contextInstanceList) {
        int i;
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(context.getTableName()).append(" (");
        int j = context.getColumnContextList().size();
        for (i = 0; i < j; ++i) {
            EntityColumnContext columnContext = context.getColumnContextList().get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(columnContext.getColumnName().toUpperCase()).append("\"");
        }
        sql.append(") VALUES ");
        j = contextInstanceList.size();
        for (i = 0; i < j; ++i) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("(");
            EntityContextInstance instance = contextInstanceList.get(i);
            int n = instance.getColumnContextInstanceList().size();
            for (int m = 0; m < n; ++m) {
                EntityColumnContextInstance columnInstance;
                if (m > 0) {
                    sql.append(",");
                }
                if ((columnInstance = instance.getColumnContextInstanceList().get(m)).getValue() == null) {
                    sql.append("NULL");
                    continue;
                }
                if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append(columnInstance.getValue());
                    continue;
                }
                if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATE, '").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(TIME, '").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                String value = ObjectUtils.toString(columnInstance.getValue());
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(")");
        }
        return GlobalBoundSql.of(sql.toString());
    }

    @Override
    public GlobalBoundSql getBatchUpdateByIdsSql(EntityContext context, List<EntityContextInstance> contextInstanceList, List<String> updateColumnList) {
        int i;
        StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
        boolean numberId = context.isNumberId();
        int j = updateColumnList.size();
        for (i = 0; i < j; ++i) {
            String updateColumn = updateColumnList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append(updateColumn).append("=CASE ID ");
            for (EntityContextInstance instance : contextInstanceList) {
                EntityColumnContextInstance columnInstance = instance.getColumnContextInstanceList().stream().filter(c -> c.getColumnContext().getColumnName().equalsIgnoreCase(updateColumn)).findAny().get();
                if (numberId) {
                    sql.append(" WHEN ").append(instance.getIdInstance().getValue()).append(" THEN ");
                } else {
                    sql.append(" WHEN '").append(instance.getIdInstance().getValue()).append("' THEN ");
                }
                if (columnInstance.getValue() == null) {
                    sql.append("NULL");
                    continue;
                }
                if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append(columnInstance.getValue());
                    continue;
                }
                if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATE, '").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(TIME, '").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                String value = ObjectUtils.toString(columnInstance.getValue());
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(" ELSE '' END");
        }
        sql.append(" WHERE ID IN (");
        j = contextInstanceList.size();
        for (i = 0; i < j; ++i) {
            EntityContextInstance instance = contextInstanceList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            Object idValue = instance.getIdInstance().getValue();
            if (numberId) {
                sql.append(idValue);
                continue;
            }
            sql.append("'").append(idValue).append("'");
        }
        sql.append(")");
        return GlobalBoundSql.of(sql.toString());
    }

    @Override
    public GlobalBoundSql getBatchInsertSql(TableContext context, List<TableContextInstance> contextInstanceList) {
        int i;
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(context.getTableName()).append(" (");
        int j = context.getColumnContextList().size();
        for (i = 0; i < j; ++i) {
            ColumnContext columnContext = context.getColumnContextList().get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(columnContext.getColumnName().toUpperCase()).append("\"");
        }
        sql.append(") VALUES ");
        j = contextInstanceList.size();
        for (i = 0; i < j; ++i) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("(");
            TableContextInstance instance = contextInstanceList.get(i);
            int n = instance.getColumnInstanceList().size();
            for (int m = 0; m < n; ++m) {
                ColumnContextInstance columnInstance;
                if (m > 0) {
                    sql.append(",");
                }
                if ((columnInstance = instance.getColumnInstanceList().get(m)).getValue() == null) {
                    sql.append("NULL");
                    continue;
                }
                if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append(columnInstance.getValue());
                    continue;
                }
                if (columnInstance.getValue() instanceof LocalDate) {
                    sql.append("CONVERT(DATE, '").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (columnInstance.getValue() instanceof LocalDateTime) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (columnInstance.getValue() instanceof Date) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (columnInstance.getValue() instanceof LocalTime) {
                    sql.append("CONVERT(TIME, '").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                String value = ObjectUtils.toString(columnInstance.getValue());
                sql.append("N'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(")");
        }
        return GlobalBoundSql.of(sql.toString());
    }

    @Override
    public GlobalBoundSql getBatchUpdateByIdsSql(TableContext context, List<TableContextInstance> contextInstanceList, List<String> updateColumnList) {
        int i;
        StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
        boolean numberId = context.isNumberId();
        int j = updateColumnList.size();
        for (i = 0; i < j; ++i) {
            String updateColumn = updateColumnList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append(updateColumn).append("=CASE ID ");
            for (TableContextInstance instance : contextInstanceList) {
                ColumnContextInstance columnInstance = instance.getColumnInstanceList().stream().filter(c -> c.getColumnContext().getColumnName().equalsIgnoreCase(updateColumn)).findAny().get();
                if (numberId) {
                    sql.append(" WHEN ").append(instance.getIdColumnInstance().getValue()).append(" THEN ");
                } else {
                    sql.append(" WHEN '").append(instance.getIdColumnInstance().getValue()).append("' THEN ");
                }
                if (columnInstance.getValue() == null) {
                    sql.append("NULL");
                    continue;
                }
                if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append(columnInstance.getValue());
                    continue;
                }
                if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATE, '").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(DATETIME, '").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("CONVERT(TIME, '").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 20)");
                    continue;
                }
                String value = ObjectUtils.toString(columnInstance.getValue());
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(" ELSE '' END");
        }
        sql.append(" WHERE ID IN (");
        j = contextInstanceList.size();
        for (i = 0; i < j; ++i) {
            TableContextInstance instance = contextInstanceList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            Object idValue = instance.getIdColumnInstance().getValue();
            if (numberId) {
                sql.append(idValue);
                continue;
            }
            sql.append("'").append(idValue).append("'");
        }
        sql.append(")");
        return GlobalBoundSql.of(sql.toString());
    }

    @Override
    public boolean containsEscapedValue(Object value) {
        if (value == null) {
            return false;
        }
        if (!(value instanceof String)) {
            return false;
        }
        String strValue = value.toString();
        return strValue.contains("%") || strValue.contains("_") || strValue.contains("'") || strValue.contains("[");
    }

    public static boolean isStringValue(Object value) {
        if (value == null) {
            return false;
        }
        return value instanceof String;
    }

    public static String valueToString(Object value) {
        if (value == null) {
            return "";
        }
        if (!(value instanceof String)) {
            return "";
        }
        return "'" + value + "'";
    }

    @Override
    public String getEscapedStrValue(Object value) {
        if (value == null) {
            return null;
        }
        String strValue = value.toString();
        if (strValue.contains("%")) {
            strValue = StringUtils.replace(strValue, "%", "\\%");
        }
        if (strValue.contains("_")) {
            strValue = StringUtils.replace(strValue, "_", "\\_");
        }
        if (strValue.contains("[")) {
            strValue = StringUtils.replace(strValue, "[", "\\[");
        }
        if (strValue.contains("'")) {
            strValue = StringUtils.replace(strValue, "'", "''");
        }
        return strValue;
    }
}
