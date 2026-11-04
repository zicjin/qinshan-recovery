package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.ColumnContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityColumnContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.exception.database.UnsupportedDataTypeJdbcException;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MysqlDialect implements Dialect {
   @Override
   public String getDatabase() {
      return "mysql";
   }

   @Override
   public String getSelectFromTableSql(String sql) {
      return "SELECT * FROM " + sql;
   }

   @Override
   public String getLimitSql(String sql, int offset, int limit) {
      return limit == 0
         ? "SELECT * FROM (\n" + sql + "\n) S_ LIMIT 0"
         : "SELECT * FROM ( SELECT S_.* FROM (\n" + sql + "\n) S_ LIMIT " + offset + "," + limit + ") R_";
   }

   @Override
   public String getSelectOptionsSql(String sql, String property) {
      if (SqlHelper.isRiskySqlSegment(property)) {
         throw new HackingDataException(property);
      } else {
         return "SELECT DISTINCT " + property + " FROM (\n" + SqlHelper.removeEndingOrderIfPresent(sql) + "\n) S_ ORDER BY " + property;
      }
   }

   @Override
   public Class<?> getJavaType(String columnName, String dataType, int scale) {
      String var4 = dataType.toLowerCase();
      switch (var4) {
         case "varchar":
         case "text":
         case "mediumtext":
         case "longtext":
         case "blob":
         case "longblob":
         case "char":
            return String.class;
         case "int":
         case "tinyint":
         case "smallint":
         case "bigint":
         case "bigint unsigned":
            return Long.class;
         case "varcharbyte":
         case "bool":
         case "bpchar":
            return String.class;
         case "bpcharbyte":
            return String.class;
         case "character":
            return String.class;
         case "float":
         case "double":
            return Double.class;
         case "decimal":
            if (scale > 0) {
               return Double.class;
            }

            return Long.class;
         case "float8":
         case "numeric":
            return Number.class;
         case "bytea":
            return String.class;
         case "int4":
            return Integer.class;
         case "int8":
            return Integer.class;
         case "date":
            return LocalDate.class;
         case "datetime":
         case "timestamp":
         case "timestamptz":
            return LocalDateTime.class;
         default:
            throw new UnsupportedDataTypeJdbcException(columnName + "@" + dataType + "@" + scale);
      }
   }

   @Override
   public boolean isClob(ColumnContext columnContext) {
      String var2 = columnContext.getDataType().toLowerCase();
      switch (var2) {
         case "text":
         case "mediumtext":
         case "longtext":
            return true;
         default:
            return false;
      }
   }

   @Override
   public boolean isBlob(ColumnContext columnContext) {
      String var2 = columnContext.getDataType().toLowerCase();
      switch (var2) {
         case "blob":
         case "longblob":
            return true;
         default:
            return false;
      }
   }

   @Override
   public String getNullReplacementClause(String column, String valueOrColumn) {
      return "IFNULL( " + column + ", " + valueOrColumn + ")";
   }

   @Override
   public String getSumColumnSql(String column, String valueOrColumn) {
      return "SUM( CAST(" + this.getNullReplacementClause(column, valueOrColumn) + " AS DECIMAL(10, 2)))";
   }

   @Override
   public String getConcatClause(String... columnOrValues) {
      StringBuilder sb = new StringBuilder("CONCAT(");
      Arrays.stream(columnOrValues).forEach(v -> sb.append("IFNULL(").append(ConvertUtils.convert(v, String.class, "")).append(",''),"));
      return sb.deleteCharAt(sb.length() - 1).append(")").toString();
   }

   @Override
   public String getSelectFirstSql(String sql) {
      return "SELECT * FROM (\n" + sql + ") R_ LIMIT 1";
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
      return 10000;
   }

   @Override
   public int getMaxParamQty() {
      return 1000000;
   }

   @Override
   public List<String> getAddColumnSqlList(String tableName, ColumnMetadata columnMetadata) {
      if (!StringUtils.startsWithIgnoreCase(columnMetadata.getColumnName(), "EXT_")) {
         throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
      } else if (SqlHelper.isRiskySqlSegment(columnMetadata.getColumnName())) {
         throw new HackingDataException(columnMetadata.getColumnName());
      } else {
         StringBuilder sql = new StringBuilder("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(columnMetadata.getColumnName()).append(" ");
         switch (columnMetadata.getDataType()) {
            case STRING:
               sql.append("VARCHAR(").append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(96)).append(") ");
               break;
            case INTEGER:
               sql.append("INT ");
               break;
            case DOUBLE:
            case FLOAT:
               sql.append("DECIMAL(")
                  .append(Optional.ofNullable(columnMetadata.getDataLength()).orElse(16))
                  .append(", ")
                  .append(Optional.ofNullable(columnMetadata.getDataScale()).orElse(6))
                  .append(") ");
               break;
            case CLOB:
               sql.append("TEXT ");
               break;
            case DATE:
               sql.append("DATE ");
               break;
            case DATETIME:
               sql.append("DATETIME ");
         }

         if (columnMetadata.isNullable()) {
            sql.append(" NULL ");
         } else {
            sql.append(" NOT NULL ");
         }

         return Arrays.asList(sql.append(" COMMENT '").append(Optional.ofNullable(columnMetadata.getComments()).orElse("")).append("'").toString());
      }
   }

   @Override
   public String getUpdateVarcharLengthSql(String tableName, String columnName, int length) {
      if (!StringUtils.startsWithIgnoreCase(columnName, "EXT_")) {
         throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
      } else if (SqlHelper.isRiskySqlSegment(tableName)) {
         throw new HackingDataException(tableName);
      } else if (SqlHelper.isRiskySqlSegment(columnName)) {
         throw new HackingDataException(columnName);
      } else {
         return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " VARCHAR(" + length + ")";
      }
   }

   @Override
   public String getDropColumnSql(String tableName, String columnName) {
      if (!StringUtils.startsWithIgnoreCase(columnName, "EXT_")) {
         throw new JdbcException("GIKAM.DATABASE.ALTER_TABLE.INVALID_PREFIX");
      } else if (SqlHelper.isRiskySqlSegment(columnName)) {
         throw new HackingDataException(columnName);
      } else {
         return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
      }
   }

   @Override
   public GlobalBoundSql getBatchInsertSql(EntityContext context, List<EntityContextInstance> contextInstanceList) {
      StringBuilder sql = new StringBuilder("INSERT INTO ").append(context.getTableName()).append(" (");
      int i = 0;

      for (int j = context.getColumnContextList().size(); i < j; i++) {
         EntityColumnContext columnContext = context.getColumnContextList().get(i);
         if (i > 0) {
            sql.append(",");
         }

         sql.append("`").append(columnContext.getColumnName().toUpperCase()).append("`");
      }

      sql.append(") VALUES ");
      i = 0;

      for (int j = contextInstanceList.size(); i < j; i++) {
         if (i > 0) {
            sql.append(",");
         }

         sql.append("(");
         EntityContextInstance instance = contextInstanceList.get(i);
         int m = 0;

         for (int n = instance.getColumnContextInstanceList().size(); m < n; m++) {
            if (m > 0) {
               sql.append(",");
            }

            EntityColumnContextInstance columnInstance = instance.getColumnContextInstanceList().get(m);
            if (DialectRepository.isKingbase()) {
               if (columnInstance.getValue() == null) {
                  sql.append("NULL");
               } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                  sql.append(columnInstance.getValue());
               } else if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
               } else if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
                  DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                  sql.append("TO_DATE('")
                     .append(DateTimeUtils.formatLocalDateTime(LocalDateTime.parse(columnInstance.getValue().toString(), formatter)))
                     .append("', '%Y-%m-%d %H:%i:%s')");
               } else if (Date.class.equals(columnInstance.getColumnContext().getType())) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
               } else if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
               } else {
                  String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                  sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
               }
            } else if (columnInstance.getValue() == null) {
               sql.append("NULL");
            } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
               if (ObjectUtils.isEmpty(columnInstance.getValue())) {
                  sql.append("NULL");
               } else if (columnInstance.getValue() instanceof Number) {
                  sql.append(columnInstance.getValue());
               } else {
                  String str = columnInstance.getValue().toString();
                  if (str.contains(".")) {
                     sql.append(NumberUtils.parseDouble(str));
                  } else {
                     sql.append(NumberUtils.parseLong(str));
                  }
               }
            } else if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
               if (ObjectUtils.isEmpty(columnInstance.getValue())) {
                  sql.append("NULL");
               } else if (columnInstance.getValue() instanceof LocalDate) {
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
               } else if (columnInstance.getValue() instanceof LocalDateTime) {
                  sql.append("STR_TO_DATE('")
                     .append(DateTimeUtils.formatLocalDate(((LocalDateTime)columnInstance.getValue()).toLocalDate()))
                     .append("', '%Y-%m-%d')");
               } else {
                  LocalDate date = DateTimeUtils.parseLocalDate(columnInstance.getValue().toString());
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDate(date)).append("', '%Y-%m-%d')");
               }
            } else if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
               if (ObjectUtils.isEmpty(columnInstance.getValue())) {
                  sql.append("NULL");
               } else if (columnInstance.getValue() instanceof LocalDate) {
                  sql.append("STR_TO_DATE('")
                     .append(DateTimeUtils.formatLocalDateTime(((LocalDate)columnInstance.getValue()).atStartOfDay()))
                     .append("', '%Y-%m-%d %H:%i:%s')");
               } else if (columnInstance.getValue() instanceof LocalDateTime) {
                  sql.append("STR_TO_DATE('")
                     .append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue()))
                     .append("', '%Y-%m-%d %H:%i:%s')");
               } else {
                  LocalDateTime dateTime = DateTimeUtils.parseLocalDateTime(columnInstance.getValue().toString());
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDateTime(dateTime)).append("', '%Y-%m-%d %H:%i:%s')");
               }
            } else if (Date.class.equals(columnInstance.getColumnContext().getType())) {
               if (ObjectUtils.isEmpty(columnInstance.getValue())) {
                  sql.append("NULL");
               } else if (columnInstance.getValue() instanceof Date) {
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
               } else {
                  Date date = DateTimeUtils.parseDate(columnInstance.getValue().toString());
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatDate(date)).append("', '%Y-%m-%d %H:%i:%s')");
               }
            } else if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
               if (ObjectUtils.isEmpty(columnInstance.getValue())) {
                  sql.append("NULL");
               } else if (columnInstance.getValue() instanceof LocalTime) {
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
               } else if (columnInstance.getValue() instanceof LocalDateTime) {
                  sql.append("STR_TO_DATE('")
                     .append(DateTimeUtils.formatLocalTime(((LocalDateTime)columnInstance.getValue()).toLocalTime()))
                     .append("', '%H:%i:%s')");
               } else {
                  LocalTime time = DateTimeUtils.parseLocalTime(columnInstance.getValue().toString());
                  sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalTime(time)).append("', '%H:%i:%s')");
               }
            } else {
               String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
               sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
         }

         sql.append(")");
      }

      return GlobalBoundSql.of(sql.toString());
   }

   @Override
   public GlobalBoundSql getBatchUpdateByIdsSql(EntityContext context, List<EntityContextInstance> contextInstanceList, List<String> updateColumnList) {
      StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
      boolean numberId = context.isNumberId();
      int i = 0;

      for (int j = updateColumnList.size(); i < j; i++) {
         String updateColumn = updateColumnList.get(i);
         if (i > 0) {
            sql.append(",");
         }

         sql.append(updateColumn).append("=CASE ID ");

         for (EntityContextInstance instance : contextInstanceList) {
            EntityColumnContextInstance columnInstance = instance.getColumnContextInstanceList()
               .stream()
               .filter(c -> c.getColumnContext().getColumnName().equalsIgnoreCase(updateColumn))
               .findAny()
               .get();
            if (numberId) {
               sql.append(" WHEN ").append(instance.getIdInstance().getValue()).append(" THEN ");
            } else {
               sql.append(" WHEN '").append(instance.getIdInstance().getValue()).append("' THEN ");
            }

            if (columnInstance.getValue() == null) {
               sql.append("NULL");
            } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
               sql.append(columnInstance.getValue());
            } else if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
            } else if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('")
                  .append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue()))
                  .append("', '%Y-%m-%d %H:%i:%s')");
            } else if (Date.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
            } else if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
            } else {
               String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
               sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
         }

         sql.append(" END");
      }

      sql.append(" WHERE ID IN (");
      i = 0;

      for (int j = contextInstanceList.size(); i < j; i++) {
         EntityContextInstance instance = contextInstanceList.get(i);
         if (i > 0) {
            sql.append(",");
         }

         Object idValue = instance.getIdInstance().getValue();
         if (numberId) {
            sql.append(idValue);
         } else {
            sql.append("'").append(idValue).append("'");
         }
      }

      sql.append(")");
      return GlobalBoundSql.of(sql.toString());
   }

   @Override
   public GlobalBoundSql getBatchInsertSql(TableContext context, List<TableContextInstance> contextInstanceList) {
      StringBuilder sql = new StringBuilder("INSERT INTO ").append(context.getTableName()).append(" (");
      int i = 0;

      for (int j = context.getColumnContextList().size(); i < j; i++) {
         ColumnContext columnContext = context.getColumnContextList().get(i);
         if (i > 0) {
            sql.append(",");
         }

         sql.append("`").append(columnContext.getColumnName().toUpperCase()).append("`");
      }

      sql.append(") VALUES ");
      i = 0;

      for (int j = contextInstanceList.size(); i < j; i++) {
         if (i > 0) {
            sql.append(",");
         }

         sql.append("(");
         TableContextInstance instance = contextInstanceList.get(i);
         int m = 0;

         for (int n = instance.getColumnInstanceList().size(); m < n; m++) {
            if (m > 0) {
               sql.append(",");
            }

            ColumnContextInstance columnInstance = instance.getColumnInstanceList().get(m);
            if (columnInstance.getValue() == null
               && !StringUtils.isEmpty(columnInstance.getColumnContext().getDefaultValue())
               && !"TIMESTAMP".equalsIgnoreCase(columnInstance.getColumnContext().getDataType())) {
               columnInstance.setValue(columnInstance.getColumnContext().getDefaultValue());
            }

            if (DialectRepository.isKingbase()) {
               if (columnInstance.getValue() == null) {
                  sql.append("NULL");
               } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
                  sql.append(columnInstance.getValue());
               } else if (columnInstance.getValue() instanceof LocalDate) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
               } else if (columnInstance.getValue() instanceof LocalDateTime) {
                  DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                  sql.append("TO_DATE('")
                     .append(DateTimeUtils.formatLocalDateTime(LocalDateTime.parse(columnInstance.getValue().toString(), formatter)))
                     .append("', '%Y-%m-%d %H:%i:%s')");
               } else if (columnInstance.getValue() instanceof Date) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
               } else if (columnInstance.getValue() instanceof LocalTime) {
                  sql.append("TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
               } else {
                  String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
                  sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
               }
            } else if (columnInstance.getValue() == null) {
               sql.append("NULL");
            } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
               sql.append(columnInstance.getValue());
            } else if (columnInstance.getValue() instanceof LocalDate) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
            } else if (columnInstance.getValue() instanceof LocalDateTime) {
               sql.append("STR_TO_DATE('")
                  .append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue()))
                  .append("', '%Y-%m-%d %H:%i:%s')");
            } else if (columnInstance.getValue() instanceof Date) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
            } else if (columnInstance.getValue() instanceof LocalTime) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
            } else {
               String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
               sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
         }

         sql.append(")");
      }

      return GlobalBoundSql.of(sql.toString());
   }

   @Override
   public GlobalBoundSql getBatchUpdateByIdsSql(TableContext context, List<TableContextInstance> contextInstanceList, List<String> updateColumnList) {
      StringBuilder sql = new StringBuilder("UPDATE ").append(context.getTableName()).append(" SET ");
      boolean numberId = context.isNumberId();
      int i = 0;

      for (int j = updateColumnList.size(); i < j; i++) {
         String updateColumn = updateColumnList.get(i);
         if (i > 0) {
            sql.append(",");
         }

         sql.append(updateColumn).append("=CASE ID ");

         for (TableContextInstance instance : contextInstanceList) {
            ColumnContextInstance columnInstance = instance.getColumnInstanceList()
               .stream()
               .filter(c -> c.getColumnContext().getColumnName().equalsIgnoreCase(updateColumn))
               .findAny()
               .get();
            if (numberId) {
               sql.append(" WHEN ").append(instance.getIdColumnInstance().getValue()).append(" THEN ");
            } else {
               sql.append(" WHEN '").append(instance.getIdColumnInstance().getValue()).append("' THEN ");
            }

            if (columnInstance.getValue() == null) {
               sql.append("NULL");
            } else if (Number.class.isAssignableFrom(columnInstance.getColumnContext().getType())) {
               sql.append(columnInstance.getValue());
            } else if (LocalDate.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalDate((LocalDate)columnInstance.getValue())).append("', '%Y-%m-%d')");
            } else if (LocalDateTime.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('")
                  .append(DateTimeUtils.formatLocalDateTime((LocalDateTime)columnInstance.getValue()))
                  .append("', '%Y-%m-%d %H:%i:%s')");
            } else if (Date.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatDate((Date)columnInstance.getValue())).append("', '%Y-%m-%d %H:%i:%s')");
            } else if (LocalTime.class.equals(columnInstance.getColumnContext().getType())) {
               sql.append("STR_TO_DATE('").append(DateTimeUtils.formatLocalTime((LocalTime)columnInstance.getValue())).append("', '%H:%i:%s')");
            } else {
               String value = ConvertUtils.convert(columnInstance.getValue(), String.class);
               sql.append("'").append(SqlHelper.getWrappedValue(value)).append("'");
            }
         }

         sql.append(" END");
      }

      sql.append(" WHERE ID IN (");
      i = 0;

      for (int j = contextInstanceList.size(); i < j; i++) {
         TableContextInstance instance = contextInstanceList.get(i);
         if (i > 0) {
            sql.append(",");
         }

         Object idValue = instance.getIdColumnInstance().getValue();
         if (numberId) {
            sql.append(idValue);
         } else {
            sql.append("'").append(idValue).append("'");
         }
      }

      sql.append(")");
      return GlobalBoundSql.of(sql.toString());
   }

   @Override
   public boolean containsEscapedValue(Object value) {
      if (value == null) {
         return false;
      } else if (!(value instanceof String)) {
         return false;
      } else {
         String strValue = value.toString();
         return strValue.contains("%") || strValue.contains("_") || strValue.contains("'");
      }
   }

   @Override
   public String getEscapedStrValue(Object value) {
      if (value == null) {
         return null;
      } else {
         String strValue = value.toString();
         if (strValue.contains("%")) {
            strValue = StringUtils.replace(strValue, "%", "\\%");
         }

         if (strValue.contains("_")) {
            strValue = StringUtils.replace(strValue, "_", "\\_");
         }

         if (strValue.contains("'")) {
            strValue = StringUtils.replace(strValue, "'", "\\'");
         }

         return strValue;
      }
   }
}
