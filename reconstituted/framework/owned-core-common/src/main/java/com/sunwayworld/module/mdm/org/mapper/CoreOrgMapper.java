/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreOrgMapper
extends GenericMapper<String> {
    public List<String> selectDescendantIdList(List<String> var1);

    public List<Map<String, Object>> selectRoleOrgList(Map<String, Object> var1);

    public List<Map<String, Object>> selectTreeNodeList(MapperParameter var1);
}
