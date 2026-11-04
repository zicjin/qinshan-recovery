/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreLesDsMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectSqlData(Map<String, String> var1);
}
