/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreMenuMapper
extends GenericMapper<String> {
    public List<Map<String, Object>> selectJsonTree(MapperParameter var1);

    public List<Map<String, Object>> selectTreeNodeList(Map<String, Object> var1);
}
