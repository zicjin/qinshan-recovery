/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreCustomTableMapper {
    public List<Map<String, Object>> selectAllTable(MapperParameter var1);

    public List<Map<String, Object>> selectColumnByTableName(@Param(value="tableName") String var1);
}
