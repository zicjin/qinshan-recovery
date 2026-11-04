/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.support.base.dao.MapDao;
import java.util.List;
import java.util.Map;

public interface MapService {
    default public MapDao getMapDao() {
        return ApplicationContextHelper.getBean(MapDao.class);
    }

    default public List<Map<String, Object>> selectAll(TableContext context) {
        return this.getMapDao().selectAll(context);
    }

    default public <ID> List<Map<String, Object>> selectListByIdList(TableContext context, List<ID> idList) {
        return this.getMapDao().selectListByIdList(context, idList, new Order[0]);
    }

    default public List<Map<String, Object>> selectList(TableContext context, Map<String, Object> item, Order ... orders) {
        return this.getMapDao().selectList(context, item, orders);
    }

    default public <ID> Map<String, Object> selectById(TableContext context, ID id) {
        return this.getMapDao().selectById(context, id);
    }

    default public <ID> Map<String, Object> selectByIdIfPresent(TableContext context, ID id) {
        return this.getMapDao().selectByIdIfPresent(context, id);
    }

    default public <ID> String selectColumnById(TableContext context, ID id, String column) {
        return this.getMapDao().selectColumnById(context, id, column, String.class);
    }

    default public <ID, V> V selectColumnById(TableContext context, ID id, String column, Class<V> columnType) {
        return this.getMapDao().selectColumnById(context, id, column, columnType);
    }

    default public <V> List<Map<String, Object>> selectListByColumnValueList(TableContext context, List<V> valueList, String colName, Order ... orders) {
        return this.getMapDao().selectListByColumnValueList(context, valueList, colName, orders);
    }

    default public <V> List<V> selectColumnList(TableContext context, Map<String, Object> item, String colName, Class<V> colType, Order ... orders) {
        return this.getMapDao().selectColumnList(context, item, colName, colType, orders);
    }

    default public void insert(TableContext context, Map<String, Object> item) {
        this.getMapDao().insert(context, item);
    }

    default public void insert(TableContext context, List<Map<String, Object>> itemList) {
        this.getMapDao().insert(context, itemList);
    }

    default public void updateIfChanged(TableContext context, Map<String, Object> item) {
        this.getMapDao().updateIfChanged(context, item);
    }

    default public void update(TableContext context, Map<String, Object> item) {
        this.getMapDao().update(context, item, new String[0]);
    }

    default public void updateIfChanged(TableContext context, List<Map<String, Object>> itemList) {
        this.getMapDao().updateIfChanged(context, itemList);
    }

    default public <ID> void delete(TableContext context, ID id) {
        this.getMapDao().delete(context, id);
    }

    default public <ID> void delete(TableContext context, List<ID> idList) {
        if (idList.isEmpty()) {
            return;
        }
        this.getMapDao().deleteByIdList(context, idList);
    }
}
