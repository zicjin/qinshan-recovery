/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.sql.Order;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface DaoMapper {
    public void insert(@Param(value="contextInstance") EntityContextInstance var1);

    public void batchInsert(@Param(value="context") EntityContext var1, @Param(value="contextInstanceList") List<EntityContextInstance> var2);

    public void deleteById(@Param(value="contextInstance") EntityContextInstance var1);

    public void batchDeleteById(@Param(value="context") EntityContext var1, @Param(value="idList") List<Object> var2);

    public List<Map<String, Object>> selectByIds(@Param(value="context") EntityContext var1, @Param(value="idList") List<Object> var2, @Param(value="selectColumnList") List<String> var3, @Param(value="orderList") List<Order> var4);

    public List<Map<String, Object>> select(@Param(value="contextInstance") EntityContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public Map<String, Object> selectFirst(@Param(value="contextInstance") EntityContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public List<Map<String, Object>> batchSelect(@Param(value="context") EntityContext var1, @Param(value="contextInstanceList") List<EntityContextInstance> var2, @Param(value="selectColumnList") List<String> var3, @Param(value="orderList") List<Order> var4);

    public List<Map<String, Object>> selectUnion(@Param(value="contextInstance") EntityContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public List<Map<String, Object>> selectByOneColumnValues(@Param(value="context") EntityContext var1, @Param(value="columnName") String var2, @Param(value="contextInstanceList") List<EntityContextInstance> var3, @Param(value="selectColumnList") List<String> var4, @Param(value="orderList") List<Order> var5);

    public void updateById(@Param(value="contextInstance") EntityContextInstance var1);

    public void updateByIds(@Param(value="contextInstance") EntityContextInstance var1, @Param(value="idList") List<Object> var2);

    public void updateByIdExt(@Param(value="contextInstance") EntityContextInstance var1, @Param(value="id") Object var2);

    public void batchUpdateByIds(@Param(value="context") EntityContext var1, @Param(value="contextInstanceList") List<EntityContextInstance> var2, @Param(value="updateColumnList") List<String> var3);

    public int count(@Param(value="contextInstance") EntityContextInstance var1);

    public int batchCount(@Param(value="context") EntityContext var1, @Param(value="contextInstanceList") List<EntityContextInstance> var2);

    public <V> List<Map<String, Object>> groupCountByValueList(@Param(value="context") EntityContext var1, @Param(value="valueList") List<V> var2, @Param(value="searchColName") String var3);

    public List<Map<String, Object>> lockByIds(@Param(value="context") EntityContext var1, @Param(value="idList") List<Object> var2);
}
