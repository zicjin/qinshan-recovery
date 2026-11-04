/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.helper;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.FieldType;
import com.sunwayworld.cloud.module.lcdp.table.helper.IndexType;
import com.sunwayworld.cloud.module.lcdp.table.helper.SqlGenerateHelper;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpDatabaseService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class LcdpTableUtils {
    private static final String cnCharacterPattren = "[\u4e00-\u9fa5]";
    private static final String[] oracleReservedWords = new String[]{"SPATIAL", "PRINT", "ELSEIF", "ROWCOUNT", "CREATE", "PURGE", "REVERT", "TRIGGER", "ROWGUIDCOL", "BETWEEN", "CLOSE", "MINUS", "CONVERT", "DENY", "EXPLAIN", "HOUR_MINUTE", "DEALLOCATE", "INNER", "SUBTYPE", "EACH", "UPDATETEXT", "OPTIONALLY", "LEFT", "SETUSER", "SIZE", "BIGINT", "RESTRICT", "RELEASE", "WHERE", "SQLWARNING", "IDENTITY", "AS", "AT", "DATABASE", "FULLTEXT", "VARCHAR", "THEN", "CONDITION", "XOR", "FREETEXTTABLE", "KEY", "UNLOCK", "ALTER", "CALL", "WAITFOR", "INTO", "SET", "REPEAT", "MERGE", "EXCEPTION", "CONSTRAINT", "RLIKE", "NOCOMPRESS", "PRECISION", "TEXTSIZE", "ASC", "CRASH", "GROUP", "DELETE", "COLAUTH", "BY", "STRAIGHT_JOIN", "RESTORE", "CHARACTER", "DUAL", "TINYINT", "RESOURCE", "LONG", "OPENDATASOURCE", "INT1", "INTERVAL", "INT2", "PROCEDURE", "STATISTICS", "INT3", "INT4", "SECOND_MICROSECOND", "ANALYZE", "INT8", "CLUSTERED", "OPEN", "CONNECTION", "CONTINUE", "DIV", "DELAYED", "NO_WRITE_TO_BINLOG", "PERCENT", "VARBINARY", "TO", "UNION", "TRUNCATE", "CURSOR", "INDEXES", "ADD", "TRY_CONVERT", "LOOP", "HIGH_PRIORITY", "VIEW", "DESC", "LINES", "VIEWS", "LOW_PRIORITY", "LABEL", "FREETEXT", "INDEX", "CURRENT_TIME", "ESCAPED", "REPLACE", "INTEGER", "VARYING", "OFFSETS", "SQL_BIG_RESULT", "LONGTEXT", "FOR", "UNIQUE", "DATABASES", "TRAILING", "TABAUTH", "ITERATE", "PIVOT", "FULL", "CURRENT", "USING", "EXEC", "NOT", "SPECIFIC", "END", "MINUTE_SECOND", "End", "FLOAT8", "HAVING", "RAISERROR", "FLOAT4", "SQLSTATE", "RECONFIGURE", "UNDO", "SEMANTICSIMILARITYTABLE", "LOAD", "TERMINATED", "BINARY", "LOCALTIME", "CONTAINS", "DROP", "RETURN", "REGEXP", "SOME", "FOREIGN", "SCHEMA", "TSEQUAL", "SQL_SMALL_RESULT", "LEAVE", "COLUMNS", "OUTER", "RULE", "SHOW", "SHUTDOWN", "INFILE", "RENAME", "EXCLUSIVE", "MOD", "EXISTS", "IDENTIFIED", "MEDIUMINT", "INTERSECT", "OPENXML", "WITH", "ESCAPE", "OVER", "GRANT", "ERRLVL", "START", "FALSE", "LINEAR", "DEFAULT", "NOCHECK", "LOCK", "JOIN", "SHARE", "TRAN", "PLAN", "BULK", "LOCALTIMESTAMP", "SESSION_USER", "NULLIF", "TABLE", "WHEN", "BREAK", "ELSE", "CLUSTER", "IF", "TYPE", "SEMANTICKEYPHRASETABLE", "DAY_MINUTE", "LONGBLOB", "NATIONAL", "IN", "DISTINCT", "OPTION", "IS", "ENCLOSED", "FUNCTION", "NOWAIT", "LEADING", "MODIFIES", "ASENSITIVE", "CASE", "OUT", "OPTIMIZE", "FORCE", "TOP", "TINYBLOB", "OVERLAPS", "CHECK", "PUBLIC", "UNPIVOT", "READTEXT", "EXIT", "WRITETEXT", "DBCC", "OPENROWSET", "GOTO", "IDENTITY_INSERT", "RAID0", "NONCLUSTERED", "DAY_HOUR", "UNSIGNED", "CASCADE", "CHAR", "CONNECT", "TRANSACTION", "BEGIN", "IGNORE", "MEDIUMTEXT", "SYSTEM_USER", "TABLESAMPLE", "OFF", "WRITE", "IDENTITYCOL", "ORDER", "MIDDLEINT", "SQL_CALC_FOUND_ROWS", "USAGE", "RIGHT", "UPDATE", "VALUES", "DOUBLE", "SAVE", "FILE", "DISTRIBUTED", "REQUIRE", "FILLFACTOR", "FETCH", "NUMERIC", "STARTING", "REVOKE", "COLLATE", "COMPUTE", "DISTINCTROW", "USE", "COPY", "SQLEXCEPTION", "LINENO", "SELECT", "EXECUTE", "OUTFILE", "PROC", "UTC_DATE", "VARCHARACTER", "CHECKPOINT", "KILL", "AUTHORIZATION", "ALL", "CURRENT_USER", "BLOB", "Null", "MODE", "COLUMN", "DECIMAL", "FROM", "SEMANTICSIMILARITYDETAILSTABLE", "X509", "COMPRESS", "BACKUP", "MINUTE_MICROSECOND", "COALESCE", "HOLDLOCK", "SECURITYAUDIT", "TINYTEXT", "DESCRIBE", "INSENSITIVE", "BOTH", "HOUR_MICROSECOND", "WITHINGROUP", "NULL", "SENSITIVE", "DAY_MICROSECOND", "SEPARATOR", "TRUE", "EXCEPT", "UTC_TIME", "SQL", "READ", "UTC_TIMESTAMP", "CLUSTERS", "LIKE", "ZEROFILL", "AND", "REAL", "INSERT", "YEAR_MONTH", "CURRENT_DATE", "DISK", "DAY_SECOND", "RANGE", "INOUT", "FLOAT", "BROWSE", "SCHEMAS", "CURRENT_TIMESTAMP", "LIMIT", "ANY", "INT", "ROLLBACK", "NATURAL", "EXTERNAL", "DUMP", "OF", "KEYS", "CHANGE", "READS", "ON", "OPENQUERY", "OR", "DEC", "COMMIT", "DETERMINISTIC", "PRIMARY", "USER", "SSL", "CONTAINSTABLE", "BEFORE", "DECLARE", "MEDIUMBLOB", "CROSS", "SMALLINT", "WHILE", "HOUR_SECOND", "MATCH", "REFERENCES"};

    public static boolean isReservedWord(String target) {
        return Arrays.stream(oracleReservedWords).anyMatch(word -> word.equalsIgnoreCase(target));
    }

    public static LcdpAnalysisResultDTO validateTable(LcdpTableDTO physicalTable, LcdpTableDTO newTable, List<LcdpTableFieldBean> fieldOpsList, List<LcdpTableIndexBean> indexOpsList) {
        LcdpAnalysisResultDTO analysisResult = new LcdpAnalysisResultDTO();
        ArrayList<String> errorRecList = new ArrayList<String>();
        analysisResult.setAnalysisResultList(errorRecList);
        analysisResult.setEnable(true);
        LcdpDatabaseService lcdpDatabaseService = ApplicationContextHelper.getBean(LcdpDatabaseService.class);
        boolean isExistPhysicalTable = !ObjectUtils.isEmpty(physicalTable);
        boolean isExistData = false;
        if (isExistPhysicalTable) {
            isExistData = lcdpDatabaseService.isExistData(physicalTable.getTableName());
        }
        if (!fieldOpsList.isEmpty()) {
            for (LcdpTableFieldBean field : fieldOpsList) {
                if (!"delete".equalsIgnoreCase(field.getFieldOperationType())) {
                    if (FieldType.varchar.name().equals(field.getFieldType())) {
                        if (!StringUtils.isEmpty(field.getDefaultValue()) && field.getDefaultValue().replaceAll("'", "").replaceAll(cnCharacterPattren, "123").length() > ConvertUtils.convert(field.getFieldLength(), Integer.class, 1)) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.DEFAULT_VALUE.TIP.INVALID", field.getFieldName()));
                            continue;
                        }
                    } else if (!FieldType.date.name().equals(field.getFieldType()) && !FieldType.clob.name().equals(field.getFieldType())) {
                        if (FieldType.number.name().equals(field.getFieldType())) {
                            if (!StringUtils.isEmpty(field.getDefaultValue())) {
                                if (!NumberUtils.isNumber(StringUtils.trim(field.getDefaultValue()))) {
                                    errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.DEFAULT_VALUE.TIP.INVALID", field.getFieldName()));
                                    continue;
                                }
                                BigDecimal defatulValue = new BigDecimal(StringUtils.trim(field.getDefaultValue()));
                                if (defatulValue.precision() > (field.getPrecision() == 0 ? 19 : field.getPrecision()) || defatulValue.scale() > field.getScale() || defatulValue.precision() - defatulValue.scale() > (field.getPrecision() == 0 ? 19 : field.getPrecision()) - field.getScale()) {
                                    errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.DEFAULT_VALUE.TIP.INVALID", field.getFieldName()));
                                    continue;
                                }
                            }
                        } else {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.TYPE.TIP.INVALID", field.getFieldName()));
                            continue;
                        }
                    }
                }
                if ("add".equalsIgnoreCase(field.getFieldOperationType()) && isExistData && StringUtils.equals("0", field.getAllowNull())) {
                    errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.ALLOW_NULL.TIP.EXIST_DATA_CANNOT_NOT_NULL", field.getFieldName()));
                    continue;
                }
                if ("update".equalsIgnoreCase(field.getFieldOperationType())) {
                    LcdpTableFieldDTO physicalField = physicalTable.getFieldList().stream().filter(f -> StringUtils.equals(f.getFieldName(), field.getFieldName())).findFirst().orElse(null);
                    if (!StringUtils.equals(physicalField.getFieldType(), field.getFieldType()) && (StringUtils.equals(field.getFieldType(), FieldType.clob.name()) || StringUtils.equals(physicalField.getFieldType(), FieldType.clob.name()))) {
                        errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.TYPE.TIP.CANNOT_UPDATE_CLOB", field.getFieldName()));
                        continue;
                    }
                    if (isExistData) {
                        if (!StringUtils.equals(physicalField.getFieldType(), field.getFieldType())) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.TYPE.TIP.EXIST_DATA_CANNOT_UPDATE", field.getFieldName()));
                            continue;
                        }
                        if (!StringUtils.equals(physicalField.getAllowNull(), field.getAllowNull()) && StringUtils.equals(field.getAllowNull(), "0") && lcdpDatabaseService.isExistNullDataInColumn(newTable.getTableName(), field.getFieldName())) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.ALLOW_NULL.TIP.EXIST_NULL_DATA_CANNOT_NOT_NULL", field.getFieldName()));
                            continue;
                        }
                        if (FieldType.varchar.name().equals(field.getFieldType()) && ConvertUtils.convert(field.getFieldLength(), Integer.class, 1) < ConvertUtils.convert(physicalField.getFieldLength(), Integer.class, 1)) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.LENGTH.TIP.EXIST_DATA_CANNOT_DECREASE", field.getFieldName()));
                            continue;
                        }
                        if (FieldType.number.name().equals(field.getFieldType()) && (physicalField.getPrecision() > field.getPrecision() || physicalField.getScale() > field.getScale())) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.PRECISION.TIP.EXIST_DATA_CANNOT_DECREASE", field.getFieldName()));
                            continue;
                        }
                    }
                }
                if (!"delete".equalsIgnoreCase(field.getFieldOperationType())) continue;
                if (lcdpDatabaseService.isExistNotNullDataInColumn(newTable.getTableName(), field.getFieldName())) {
                    errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.TYPE.TIP.EXIST_DATA_CANNOT_DELETE", field.getFieldName()));
                    continue;
                }
                for (LcdpTableIndexDTO index : newTable.getIndexList()) {
                    String[] indexFieldList;
                    if (StringUtils.isEmpty(index.getIndexField()) || !Arrays.stream(indexFieldList = index.getIndexField().split(",")).anyMatch(indexField -> StringUtils.equals(indexField, field.getFieldName()))) continue;
                    errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.FIELD.TYPE.TIP.INDEX_COLUMN_CANNOT_DELETE", field.getFieldName()));
                }
            }
        }
        if (!indexOpsList.isEmpty()) {
            for (LcdpTableIndexBean index : indexOpsList) {
                if (!"add".equalsIgnoreCase(index.getIndexOperationType())) continue;
                if (!StringUtils.isEmpty(index.getIndexField())) {
                    String[] indexFieldList;
                    for (String indexFieldName : indexFieldList = index.getIndexField().split(",")) {
                        LcdpTableFieldDTO indexFiled = newTable.getFieldList().stream().filter(oldField -> StringUtils.equals(indexFieldName, oldField.getFieldName())).findAny().orElse(null);
                        if (ObjectUtils.isEmpty(indexFiled)) {
                            errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.INDEX.FIELD.TIP.NOT_EXIST", index.getIndexName()));
                            continue;
                        }
                        if (!StringUtils.equals(indexFiled.getFieldType(), FieldType.clob.name())) continue;
                        errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.INDEX.FIELD.TIP.NOT_SUPPORT_CLOB", index.getIndexName()));
                    }
                    continue;
                }
                errorRecList.add(I18nHelper.getMessage("LCDP.MODULE.TABLES.INDEX.FIELD.TIP.NOT_EXIST", index.getIndexName()));
            }
        }
        if (!errorRecList.isEmpty()) {
            analysisResult.setEnable(false);
        }
        return analysisResult;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean validateDatabaseConsistency(String tableName) {
        if (!ApplicationContextHelper.isProfileActivated("lcdp-tenant")) {
            return true;
        }
        LcdpDatabaseService lcdpDatabaseService = ApplicationContextHelper.getBean(LcdpDatabaseService.class);
        String tenant = TenantContext.getTenant();
        try {
            TenantContext.removeTenant();
            boolean existPhysicalTable = lcdpDatabaseService.isExistPhysicalTable(tableName);
            TenantContext.setTenant("lcdp");
            boolean existPhysicalTableDev = lcdpDatabaseService.isExistPhysicalTable(tableName);
            if (existPhysicalTable != existPhysicalTableDev) {
                boolean bl = false;
                return bl;
            }
            if (!existPhysicalTable) {
                boolean bl = true;
                return bl;
            }
            LcdpTableDTO lcdpTableDTODev = lcdpDatabaseService.selectPhysicalTableInfo(tableName);
            TenantContext.removeTenant();
            LcdpTableDTO lcdpTableDTO = lcdpDatabaseService.selectPhysicalTableInfo(tableName);
            List<LcdpTableFieldBean> fieldOpsList = LcdpTableUtils.analyzeFieldOps(lcdpTableDTODev.getFieldList(), lcdpTableDTO.getFieldList());
            List<LcdpTableIndexBean> IndexOpsList = LcdpTableUtils.analyzeIndexOps(lcdpTableDTODev.getIndexList(), lcdpTableDTO.getIndexList());
            if (fieldOpsList.size() > 0 || IndexOpsList.size() > 0) {
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            TenantContext.setTenant(tenant);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean validateViewConsistency(String viewName) {
        if (!ApplicationContextHelper.isProfileActivated("lcdp-tenant")) {
            return true;
        }
        LcdpDatabaseService lcdpDatabaseService = ApplicationContextHelper.getBean(LcdpDatabaseService.class);
        String tenant = TenantContext.getTenant();
        try {
            TenantContext.removeTenant();
            boolean existPhysicalTable = lcdpDatabaseService.isExistPhysicalView(viewName);
            TenantContext.setTenant("lcdp");
            boolean existPhysicalTableDev = lcdpDatabaseService.isExistPhysicalView(viewName);
            if (existPhysicalTable != existPhysicalTableDev) {
                boolean bl = false;
                return bl;
            }
            if (!existPhysicalTable) {
                boolean bl = true;
                return bl;
            }
            LcdpViewBean lcdpViewDev = lcdpDatabaseService.selectPhysicalViewInfo(viewName);
            TenantContext.removeTenant();
            LcdpViewBean lcdpViewPro = lcdpDatabaseService.selectPhysicalViewInfo(viewName);
            boolean bl = StringUtils.equalsIgnoreCase(lcdpViewDev.getSelectStatement(), lcdpViewPro.getSelectStatement());
            return bl;
        }
        finally {
            TenantContext.setTenant(tenant);
        }
    }

    public static List<LcdpTableFieldBean> analyzeFieldOps(List<LcdpTableFieldDTO> fieldList, List<LcdpTableFieldDTO> physicalFieldList) {
        ArrayList<LcdpTableFieldBean> fieldOpsList = new ArrayList<LcdpTableFieldBean>();
        SqlGenerateHelper sqlGenerateHelper = ApplicationContextHelper.getBean(SqlGenerateHelper.class);
        Optional.ofNullable(fieldList).orElse(Collections.emptyList()).forEach(field -> {
            LcdpTableFieldBean fieldOps = new LcdpTableFieldBean();
            BeanUtils.copyProperties(field, (Object)fieldOps, new String[0]);
            Optional.ofNullable(physicalFieldList).orElse(Collections.emptyList()).forEach(currentField -> {
                LcdpTableFieldBean currentFieldBean = new LcdpTableFieldBean();
                BeanUtils.copyProperties(currentField, (Object)currentFieldBean, new String[0]);
                if (!(!StringUtils.equals(currentField.getFieldName(), field.getFieldName()) || StringUtils.equals(sqlGenerateHelper.parseFieldType(fieldOps), sqlGenerateHelper.parseFieldType(currentFieldBean)) && StringUtils.equals(fieldOps.getAllowNull(), currentFieldBean.getAllowNull()) && StringUtils.equals(sqlGenerateHelper.parseDefaultValue(fieldOps), sqlGenerateHelper.parseDefaultValue(currentFieldBean)) && StringUtils.equals(sqlGenerateHelper.nullToEmpty(fieldOps.getFieldComment()), sqlGenerateHelper.nullToEmpty(currentFieldBean.getFieldComment())))) {
                    fieldOps.setFieldOperationType("update");
                    fieldOpsList.add(fieldOps);
                }
            });
        });
        Optional.ofNullable(fieldList).orElse(Collections.emptyList()).stream().filter(field -> Optional.ofNullable(physicalFieldList).orElse(Collections.emptyList()).stream().noneMatch(currentField -> StringUtils.equals(field.getFieldName(), currentField.getFieldName()))).forEach(field -> {
            LcdpTableFieldBean fieldOps = new LcdpTableFieldBean();
            BeanUtils.copyProperties(field, (Object)fieldOps, new String[0]);
            fieldOps.setFieldOperationType("add");
            fieldOpsList.add(fieldOps);
        });
        Optional.ofNullable(physicalFieldList).orElse(Collections.emptyList()).stream().filter(physicalField -> Optional.ofNullable(fieldList).orElse(Collections.emptyList()).stream().noneMatch(field -> StringUtils.equals(field.getFieldName(), physicalField.getFieldName()))).forEach(physicalField -> {
            LcdpTableFieldBean fieldOps = new LcdpTableFieldBean();
            BeanUtils.copyProperties(physicalField, (Object)fieldOps, new String[0]);
            fieldOps.setFieldOperationType("delete");
            fieldOpsList.add(fieldOps);
        });
        return fieldOpsList;
    }

    public static List<LcdpTableIndexBean> analyzeIndexOps(List<LcdpTableIndexDTO> indexList, List<LcdpTableIndexDTO> physicalIndexList) {
        ArrayList<LcdpTableIndexBean> indexOpsList = new ArrayList<LcdpTableIndexBean>();
        Optional.ofNullable(indexList).orElse(Collections.emptyList()).stream().filter(index -> !IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && Optional.ofNullable(physicalIndexList).orElse(Collections.emptyList()).stream().noneMatch(physicalIndex -> StringUtils.equals(index.getIndexField(), physicalIndex.getIndexField())) || IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()) && Optional.ofNullable(physicalIndexList).orElse(Collections.emptyList()).stream().noneMatch(physicalIndex -> IndexType.primarykey.name().equalsIgnoreCase(index.getIndexType()))).forEach(index -> {
            LcdpTableIndexBean indexOps = new LcdpTableIndexBean();
            BeanUtils.copyProperties(index, (Object)indexOps, new String[0]);
            indexOps.setIndexOperationType("add");
            indexOpsList.add(indexOps);
        });
        Optional.ofNullable(physicalIndexList).orElse(Collections.emptyList()).stream().filter(physicalIndex -> !IndexType.primarykey.name().equalsIgnoreCase(physicalIndex.getIndexType()) && Optional.ofNullable(indexList).orElse(Collections.emptyList()).stream().noneMatch(index -> StringUtils.equals(index.getIndexField(), physicalIndex.getIndexField()))).forEach(physicalIndex -> {
            LcdpTableIndexBean indexOps = new LcdpTableIndexBean();
            BeanUtils.copyProperties(physicalIndex, (Object)indexOps, new String[0]);
            indexOps.setIndexOperationType("delete");
            indexOpsList.add(indexOps);
        });
        return indexOpsList;
    }

    public static List<LcdpTableDTO> parseTableBeanToDTO(List<LcdpTableBean> tableList, List<LcdpTableFieldBean> fieldList, List<LcdpTableIndexBean> indexList) {
        if (ObjectUtils.isEmpty(tableList) || tableList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        Map<String, List<LcdpTableFieldBean>> fieldGroupMap = Optional.ofNullable(fieldList).orElse(Collections.emptyList()).stream().collect(Collectors.groupingBy(LcdpTableFieldBean::getTableName));
        Map<String, List<LcdpTableIndexBean>> indexGroupMap = Optional.ofNullable(indexList).orElse(Collections.emptyList()).stream().collect(Collectors.groupingBy(LcdpTableIndexBean::getTableName));
        return tableList.stream().map(lcdpTable -> {
            LcdpTableDTO table = new LcdpTableDTO();
            BeanUtils.copyProperties(lcdpTable, (Object)table, new String[0]);
            table.setFieldList(Optional.ofNullable(fieldGroupMap.get(lcdpTable.getTableName())).orElse(Collections.emptyList()).stream().map(lcdpField -> {
                LcdpTableFieldDTO field = new LcdpTableFieldDTO();
                BeanUtils.copyProperties(lcdpField, (Object)field, new String[0]);
                return field;
            }).collect(Collectors.toList()));
            table.setIndexList(Optional.ofNullable(indexGroupMap.get(lcdpTable.getTableName())).orElse(Collections.emptyList()).stream().map(lcdpIndex -> {
                LcdpTableIndexDTO index = new LcdpTableIndexDTO();
                BeanUtils.copyProperties(lcdpIndex, (Object)index, new String[0]);
                return index;
            }).collect(Collectors.toList()));
            return table;
        }).collect(Collectors.toList());
    }

    public static String i18nCode(String tableName, String fieldName) {
        return tableName.toUpperCase() + "." + (StringUtils.isEmpty(fieldName) ? "SERVICE_NAME" : fieldName.toUpperCase());
    }
}
