/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.helper;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.helper.FieldType;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.text.MessageFormat;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public interface SqlGenerateHelper {
    public String generateTableCommentSql(LcdpTableBean var1, LcdpTableDTO var2);

    public String generateIndexSql(LcdpTableBean var1, LcdpTableIndexBean var2);

    public List<String> generateColumnSql(LcdpTableBean var1, LcdpTableFieldBean var2, LcdpTableFieldBean var3);

    public List<String> generateCreateTableSql(LcdpTableBean var1, List<LcdpTableFieldBean> var2, LcdpTableIndexBean var3);

    default public String generateDeleteSql(LcdpTableBean table) {
        return MessageFormat.format("DROP TABLE {0}", table.getTableName());
    }

    public String databaseTypeToFieldType(String var1);

    default public String nullToEmpty(String source) {
        return StringUtils.isEmpty(source) ? "" : source;
    }

    default public String parseIsNULL(LcdpTableFieldBean field) {
        if ("1".equals(field.getAllowNull())) {
            return "NULL";
        }
        if ("0".equals(field.getAllowNull())) {
            return "NOT NULL";
        }
        return "";
    }

    default public String parseDefaultValue(LcdpTableFieldBean field) {
        if (!StringUtils.isEmpty(field.getDefaultValue()) && (FieldType.varchar.name().equals(field.getFieldType()) || FieldType.clob.name().equals(field.getFieldType()))) {
            field.setDefaultValue("'" + field.getDefaultValue().trim().replaceAll("'", "") + "'");
        }
        return StringUtils.isEmpty(field.getDefaultValue()) ? "" : MessageFormat.format("DEFAULT {0}", field.getDefaultValue());
    }

    public String parseFieldType(LcdpTableFieldBean var1);

    default public String parseIndexField(LcdpTableIndexBean index) {
        String indexField = index.getIndexField();
        if (ObjectUtils.isEmpty(indexField)) {
            return "";
        }
        String reservedIdentifier = this.getReservedIdentifier();
        StringTokenizer indexFieldTokenizer = new StringTokenizer(indexField, ",");
        StringJoiner stringJoiner = new StringJoiner(",");
        while (indexFieldTokenizer.hasMoreTokens()) {
            stringJoiner.add(reservedIdentifier + indexFieldTokenizer.nextToken() + reservedIdentifier);
        }
        return stringJoiner.toString();
    }

    default public String getReservedIdentifier() {
        return "\"";
    }

    default public String generateCreateViewSql(LcdpViewBean view) {
        return MessageFormat.format("CREATE VIEW {0} AS {1}", view.getTableName(), view.getSelectStatement());
    }

    default public String generateAlterViewSql(LcdpViewBean view) {
        return MessageFormat.format("CREATE OR REPLACE VIEW {0} AS {1}", view.getTableName(), view.getSelectStatement());
    }

    default public String generateDropViewSql(LcdpViewBean view) {
        return MessageFormat.format("DROP VIEW {0}", view.getViewName());
    }

    public String parseSQLException(String var1);
}
