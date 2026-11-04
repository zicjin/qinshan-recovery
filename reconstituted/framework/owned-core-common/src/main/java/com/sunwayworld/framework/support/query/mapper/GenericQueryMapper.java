/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.query.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import java.util.List;
import java.util.Map;

public interface GenericQueryMapper {
    public List<Map<String, Object>> selectByCondition(MapperParameter var1);
}
