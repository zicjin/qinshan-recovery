/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericMapper<ID extends Serializable> {
    public List<Map<String, Object>> selectByCondition(MapperParameter var1);
}
