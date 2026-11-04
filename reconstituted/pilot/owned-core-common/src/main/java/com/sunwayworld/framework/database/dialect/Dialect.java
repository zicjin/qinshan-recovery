/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.dialect.ColumnMetadata;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import java.util.List;

public interface Dialect {
    public static final String EXTRA_COLUMN_PREFIX = "EXT_";

    public String getDatabase();

    public String getSelectFromTableSql(String var1);

    public String getLimitSql(String var1, int var2, int var3);

    public String getSelectOptionsSql(String var1, String var2);

    public Class<?> getJavaType(String var1, String var2, int var3);

    public boolean isClob(ColumnContext var1);

    public boolean isBlob(ColumnContext var1);

    default public boolean isTimestamp(ColumnContext columnContext) {
        return false;
    }

    public String getNullReplacementClause(String var1, String var2);

    public String getSumColumnSql(String var1, String var2);

    public String getConcatClause(String ... var1);

    public String getSelectFirstSql(String var1);

    public String getSelectTodoSql(String var1);

    public String getSelectNoResultSql();

    public int getInClauseIdMaxQty();

    public int getMaxParamQty();

    default public int getMaxBatchInsertQty() {
        return 2000;
    }

    public List<String> getAddColumnSqlList(String var1, ColumnMetadata var2);

    public String getUpdateVarcharLengthSql(String var1, String var2, int var3);

    public String getDropColumnSql(String var1, String var2);

    public GlobalBoundSql getBatchInsertSql(EntityContext var1, List<EntityContextInstance> var2);

    public GlobalBoundSql getBatchUpdateByIdsSql(EntityContext var1, List<EntityContextInstance> var2, List<String> var3);

    public GlobalBoundSql getBatchInsertSql(TableContext var1, List<TableContextInstance> var2);

    public GlobalBoundSql getBatchUpdateByIdsSql(TableContext var1, List<TableContextInstance> var2, List<String> var3);

    public boolean containsEscapedValue(Object var1);

    public String getEscapedStrValue(Object var1);
}
