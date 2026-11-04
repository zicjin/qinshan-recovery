/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.context.ApplicationContextHelper;
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
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.exception.database.UnsupportedDataTypeJdbcException;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class OracleDialect
implements Dialect {
    @Override
    public String getDatabase() {
        return "oracle";
    }

    @Override
    public String getSelectFromTableSql(String table) {
        return "SELECT * FROM " + table;
    }

    @Override
    public String getLimitSql(String sql, int offset, int limit) {
        if (limit == 0) {
            return "SELECT * FROM (\n" + sql + "\n) S_ WHERE ROWNUM = 0";
        }
        return "SELECT * FROM ( SELECT S_.*, ROWNUM RN_ FROM (\n" + sql + "\n) S_ WHERE ROWNUM <= " + (offset + limit) + ") WHERE RN_ > " + offset;
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
        switch (dataType.toLowerCase()) {
            case "varchar":
            case "varchar2":
            case "nvarchar2":
            case "nvarchar":
            case "blob":
            case "clob":
            case "nclob":
            case "char":
            case "text":
            case "name":
            case "varcharbyte": {
                return String.class;
            }
            case "number":
            case "dec":
            case "decimal":
            case "bigint":
            case "integer":
            case "int":
            case "float":
            case "real":
            case "numeric": {
                return Number.class;
            }
            case "bytea": {
                return String.class;
            }
            case "int4": {
                return Integer.class;
            }
            case "int8": {
                return Integer.class;
            }
            case "double": {
                if (scale > 0) {
                    return Double.class;
                }
                return Long.class;
            }
            case "date": {
                return LocalDateTime.class;
            }
            case "datetime": {
                return LocalDateTime.class;
            }
        }
        if (StringUtils.startsWithIgnoreCase(dataType, "timestamp")) {
            return LocalDateTime.class;
        }
        throw new UnsupportedDataTypeJdbcException(columnName + "@" + dataType + "@" + scale);
    }

    @Override
    public boolean isClob(ColumnContext columnContext) {
        switch (columnContext.getDataType().toLowerCase()) {
            case "clob": {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBlob(ColumnContext columnContext) {
        switch (columnContext.getDataType().toLowerCase()) {
            case "blob": {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getNullReplacementClause(String column, String valueOrColumn) {
        return "NVL( " + column + ", " + valueOrColumn + ")";
    }

    @Override
    public String getSumColumnSql(String column, String valueOrColumn) {
        return "SUM(" + this.getNullReplacementClause(column, valueOrColumn) + ")";
    }

    @Override
    public String getConcatClause(String ... columnOrValues) {
        return ArrayUtils.arrayToDelimitedString(columnOrValues, " || ");
    }

    @Override
    public String getSelectFirstSql(String sql) {
        return "SELECT * FROM (\n" + sql + ") WHERE ROWNUM = 1";
    }

    @Override
    public String getSelectTodoSql(String todoSql) {
        return "SELECT T.MENUID, T.MODELNAME, T.HREF, COUNT(1) QTY\n" + "  FROM (" + todoSql + ") T\n" + " GROUP BY T.MENUID, T.MODELNAME, T.HREF";
    }

    @Override
    public String getSelectNoResultSql() {
        return "SELECT 1 FROM (SELECT 1 AS ID FROM DUAL) T WHERE T.ID = 0";
    }

    @Override
    public int getInClauseIdMaxQty() {
        return 400;
    }

    @Override
    public int getMaxParamQty() {
        return 5000;
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
                addSql.append("VARCHAR2(").append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(96)).append(") ");
                break;
            }
            case INTEGER: {
                addSql.append("INT ");
                break;
            }
            case DOUBLE:
            case FLOAT: {
                addSql.append("DECIMAL(").append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(16)).append(", ").append(Optional.ofNullable(columnMetadata.getDataScale()).orElse(6)).append(") ");
                break;
            }
            case CLOB: {
                addSql.append("CLOB ");
                break;
            }
            case DATE: {
                addSql.append("DATE ");
                break;
            }
            case DATETIME: {
                addSql.append("TIMESTAMP ");
                break;
            }
        }
        if (!columnMetadata.isNullable()) {
            addSql.append(" NOT NULL ");
        }
        StringBuilder commentSql = new StringBuilder("COMMENT ON COLUMN ").append(tableName).append(".").append(columnMetadata.getColumnName()).append(" is '").append(StringUtils.replace(columnMetadata.getComments(), "'", "")).append("'");
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
        return "ALTER TABLE " + tableName + " MODIFY " + columnName + " VARCHAR2(" + length + ")";
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
        HashMap<String, String> paramMap = new HashMap<String, String>();
        StringBuilder sql = new StringBuilder("BEGIN ");
        int j = contextInstanceList.size();
        for (int i = 0; i < j; ++i) {
            sql.append("INSERT INTO ").append(context.getTableName()).append(" (");
            int n = context.getColumnContextList().size();
            for (int m = 0; m < n; ++m) {
                EntityColumnContext columnContext = context.getColumnContextList().get(m);
                if (m > 0) {
                    sql.append(",");
                }
                sql.append("\"").append(columnContext.getColumnName().toUpperCase()).append("\"");
            }
            sql.append(") VALUES (");
            EntityContextInstance instance = contextInstanceList.get(i);
            int n2 = instance.getColumnContextInstanceList().size();
            for (int m = 0; m < n2; ++m) {
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
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 'yyyy-mm-dd')");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 'hh24:mi:ss')");
                    continue;
                }
                String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                if (columnInstance.getColumnContext().isClob() || value.length() > 1000) {
                    boolean enabledKingbase = ApplicationContextHelper.getEnvironment().getProperty("kingbase.enabled", Boolean.TYPE);
                    if (enabledKingbase) {
                        String key = columnInstance.getColumnContext().getColumnName() + "_" + i + "_" + m;
                        sql.append("#{").append(key).append(",jdbcType=CLOB}");
                        paramMap.put(key, value);
                        continue;
                    }
                    sql.append("TO_CLOB('").append(SqlHelper.getWrappedValue(value)).append("')");
                    continue;
                }
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(");");
        }
        sql.append("END;");
        GlobalBoundSql boundSql = GlobalBoundSql.of(sql.toString());
        boundSql.putAll(paramMap);
        return boundSql;
    }

    @Override
    public GlobalBoundSql getBatchUpdateByIdsSql(EntityContext context, List<EntityContextInstance> contextInstanceList, List<String> updateColumnList) {
        int i;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
        boolean numberId = context.isNumberId();
        int j = updateColumnList.size();
        for (i = 0; i < j; ++i) {
            String updateColumn = updateColumnList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append(updateColumn).append("=CASE ID ");
            int n = contextInstanceList.size();
            for (int m = 0; m < n; ++m) {
                EntityContextInstance instance = contextInstanceList.get(m);
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
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 'yyyy-mm-dd')");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 'hh24:mi:ss')");
                    continue;
                }
                String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                if (columnInstance.getColumnContext().isClob() || value.length() > 1000) {
                    boolean enabledKingbase = ApplicationContextHelper.getEnvironment().getProperty("kingbase.enabled", Boolean.TYPE);
                    if (enabledKingbase) {
                        String key = columnInstance.getColumnContext().getColumnName() + "_" + i + "_" + m;
                        sql.append("#{").append(key).append(",jdbcType=CLOB}");
                        paramMap.put(key, value);
                        continue;
                    }
                    sql.append("TO_CLOB('").append(SqlHelper.getWrappedValue(value)).append("')");
                    continue;
                }
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(" END");
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
        GlobalBoundSql boundSql = GlobalBoundSql.of(sql.toString());
        boundSql.putAll(paramMap);
        return boundSql;
    }

    @Override
    public GlobalBoundSql getBatchInsertSql(TableContext context, List<TableContextInstance> contextInstanceList) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        StringBuilder sql = new StringBuilder("BEGIN ");
        int j = contextInstanceList.size();
        for (int i = 0; i < j; ++i) {
            sql.append("INSERT INTO ").append(context.getTableName()).append(" (");
            int n = context.getColumnContextList().size();
            for (int m = 0; m < n; ++m) {
                ColumnContext columnContext = context.getColumnContextList().get(m);
                if (m > 0) {
                    sql.append(",");
                }
                sql.append("\"").append(columnContext.getColumnName().toUpperCase()).append("\"");
            }
            sql.append(") VALUES (");
            TableContextInstance instance = contextInstanceList.get(i);
            int n2 = instance.getColumnInstanceList().size();
            for (int m = 0; m < n2; ++m) {
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
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 'yyyy-mm-dd')");
                    continue;
                }
                if (columnInstance.getValue() instanceof LocalDateTime) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (columnInstance.getValue() instanceof Date) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (columnInstance.getValue() instanceof LocalTime) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 'hh24:mi:ss')");
                    continue;
                }
                String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                if (columnInstance.getColumnContext().isClob() || value.length() > 1000) {
                    boolean enabledKingbase = ApplicationContextHelper.getEnvironment().getProperty("kingbase.enabled", Boolean.TYPE);
                    if (enabledKingbase) {
                        String key = columnInstance.getColumnContext().getColumnName() + "_" + i + "_" + m;
                        sql.append("#{").append(key).append(",jdbcType=CLOB}");
                        paramMap.put(key, value);
                        continue;
                    }
                    sql.append("TO_CLOB('").append(SqlHelper.getWrappedValue(value)).append("')");
                    continue;
                }
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(");");
        }
        sql.append("END;");
        GlobalBoundSql boundSql = GlobalBoundSql.of(sql.toString());
        boundSql.putAll(paramMap);
        return boundSql;
    }

    @Override
    public GlobalBoundSql getBatchUpdateByIdsSql(TableContext context, List<TableContextInstance> contextInstanceList, List<String> updateColumnList) {
        int i;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
        boolean numberId = context.isNumberId();
        int j = updateColumnList.size();
        for (i = 0; i < j; ++i) {
            String updateColumn = updateColumnList.get(i);
            if (i > 0) {
                sql.append(",");
            }
            sql.append(updateColumn).append("=CASE ID ");
            int n = contextInstanceList.size();
            for (int m = 0; m < n; ++m) {
                TableContextInstance instance = contextInstanceList.get(m);
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
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', 'yyyy-mm-dd')");
                    continue;
                }
                if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (Date.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', 'yyyy-mm-dd hh24:mi:ss')");
                    continue;
                }
                if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                    sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', 'hh24:mi:ss')");
                    continue;
                }
                String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                if (columnInstance.getColumnContext().isClob() || value.length() > 1000) {
                    if (DialectRepository.isKingbase()) {
                        String key = columnInstance.getColumnContext().getColumnName() + "_" + i + "_" + m;
                        sql.append("#{").append(key).append(",jdbcType=CLOB}");
                        paramMap.put(key, value);
                        continue;
                    }
                    sql.append("TO_CLOB('").append(SqlHelper.getWrappedValue(value)).append("')");
                    continue;
                }
                sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
            sql.append(" END");
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
        GlobalBoundSql boundSql = GlobalBoundSql.of(sql.toString());
        boundSql.putAll(paramMap);
        return boundSql;
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
        return strValue.contains("%") || strValue.contains("_") || strValue.contains("'");
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
        if (strValue.contains("'")) {
            strValue = StringUtils.replace(strValue, "'", "''");
        }
        return strValue;
    }
}
