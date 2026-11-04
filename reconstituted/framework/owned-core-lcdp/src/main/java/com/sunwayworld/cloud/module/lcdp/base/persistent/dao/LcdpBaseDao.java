/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.base.persistent.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import java.util.List;
import java.util.Map;

public interface LcdpBaseDao {
    public void insert(String var1, Map<String, Object> var2);

    public void insert(String var1, List<Map<String, Object>> var2);

    public List<Map<String, Object>> selectByCondition(MapperParameter var1);

    public void delete(String var1, List<String> var2);

    public void update(String var1, Map<String, Object> var2);

    public void update(String var1, List<Map<String, Object>> var2, String ... var3);

    public void updateIfChanged(String var1, List<Map<String, Object>> var2);
}
