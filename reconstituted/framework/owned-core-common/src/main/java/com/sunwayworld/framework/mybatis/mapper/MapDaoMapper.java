/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.database.sql.Order;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

interface MapDaoMapper {
    public void insert(@Param(value="instance") TableContextInstance var1);

    public void insertList(@Param(value="context") TableContext var1, @Param(value="instanceList") List<TableContextInstance> var2);

    public void updateById(@Param(value="instance") TableContextInstance var1);

    public <ID> void updateByIdList(@Param(value="instance") TableContextInstance var1, @Param(value="idList") List<ID> var2);

    public void updateItemList(@Param(value="context") TableContext var1, @Param(value="instanceList") List<TableContextInstance> var2, @Param(value="updateColumnList") List<String> var3);

    public <ID> void deleteById(@Param(value="context") TableContext var1, @Param(value="id") ID var2);

    public <ID> void deleteByIdList(@Param(value="context") TableContext var1, @Param(value="idList") List<ID> var2);

    public <ID> List<Map<String, Object>> selectByIdList(@Param(value="context") TableContext var1, @Param(value="idList") List<ID> var2, @Param(value="selectColumnList") List<String> var3, @Param(value="orderList") List<Order> var4);

    public List<Map<String, Object>> selectByInstance(@Param(value="instance") TableContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public Map<String, Object> selectFirstByInstance(@Param(value="instance") TableContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public List<Map<String, Object>> selectByInstanceList(@Param(value="context") TableContext var1, @Param(value="instanceList") List<TableContextInstance> var2, @Param(value="selectColumnList") List<String> var3, @Param(value="orderList") List<Order> var4);

    public List<Map<String, Object>> selectUnionByInstance(@Param(value="instance") TableContextInstance var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public List<Map<String, Object>> selectAll(@Param(value="context") TableContext var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public Cursor<Map<String, Object>> selectAllWithCursor(@Param(value="context") TableContext var1, @Param(value="selectColumnList") List<String> var2, @Param(value="orderList") List<Order> var3);

    public int countByInstance(@Param(value="instance") TableContextInstance var1);

    public int countUnionByInstanceList(@Param(value="context") TableContext var1, @Param(value="instanceList") List<TableContextInstance> var2);
}
