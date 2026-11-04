/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import java.util.List;
import java.util.Map;

public interface LcdpDatabaseDao {
    public List<Map<String, Object>> selectFieldListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectIndexListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectTableNameListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectTableInfoByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectViewInfoListByCondition(MapperParameter var1);

    public int countByTableName(String var1);

    public int countByViewName(String var1);

    public int countByIndexName(String var1);

    public void lockTableByTableName(String var1);

    public List<Map<String, Object>> selectTableOrViewNameListByCondition(MapperParameter var1);
}
