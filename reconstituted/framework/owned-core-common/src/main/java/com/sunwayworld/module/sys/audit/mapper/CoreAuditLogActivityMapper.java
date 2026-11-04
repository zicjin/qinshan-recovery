/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreAuditLogActivityMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectAuditLogActivityByCondition(MapperParameter var1);
}
