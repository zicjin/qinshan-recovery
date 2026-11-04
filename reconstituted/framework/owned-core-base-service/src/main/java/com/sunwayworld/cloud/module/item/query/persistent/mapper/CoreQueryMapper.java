/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreQueryMapper {
    public List<Map<String, Object>> selectByCondition(MapperParameter var1);
}
