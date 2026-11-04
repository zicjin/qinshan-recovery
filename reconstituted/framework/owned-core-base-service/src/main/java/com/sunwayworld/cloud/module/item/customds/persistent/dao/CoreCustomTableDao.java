/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import java.util.List;
import java.util.Map;

public interface CoreCustomTableDao {
    public List<Map<String, Object>> selectAllTable(MapperParameter var1);

    public List<Map<String, Object>> selectColumnByTableName(String var1);
}
