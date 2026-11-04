/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface LcdpModulePageI18nMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectAllI18nMessage(MapperParameter var1);

    public List<Map<String, Object>> selectEffectiveByCondition(MapperParameter var1);
}
