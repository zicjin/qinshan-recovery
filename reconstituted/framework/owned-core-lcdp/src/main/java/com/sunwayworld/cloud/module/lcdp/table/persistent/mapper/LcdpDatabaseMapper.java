/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface LcdpDatabaseMapper {
    public List<Map<String, Object>> selectFieldListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectIndexListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectTableNameListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectTableInfoByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectViewInfoListByCondition(MapperParameter var1);

    public int countByTableName(@Param(value="tableName") String var1);

    public int countByViewName(@Param(value="viewName") String var1);

    public int countByIndexName(@Param(value="indexName") String var1);

    public List<Map<String, Object>> lockTableByTableName(@Param(value="tableName") String var1);

    public List<Map<String, Object>> selectTableOrViewNameListByCondition(MapperParameter var1);
}
