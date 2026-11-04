/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface LcdpResourceMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectTreeNodeList(Map<String, Object> var1);

    public List<Map<String, Object>> selectTreeChildQtyByParentId(@Param(value="parentIdList") List<Long> var1);

    public List<Map<String, Object>> selectPageByCondition(MapperParameter var1);
}
