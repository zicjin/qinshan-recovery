/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAdminMybatisStatementMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectActiveList(@Param(value="menuId") String var1, @Param(value="mapperId") String var2);
}
