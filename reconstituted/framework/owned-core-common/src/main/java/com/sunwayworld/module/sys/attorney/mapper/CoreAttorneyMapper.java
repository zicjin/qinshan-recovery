/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAttorneyMapper
extends GenericMapper<Long> {
    public List<Long> selectExecutableAttorneyIdList(@Param(value="trustorId") String var1, @Param(value="trusteeId") String var2);

    public List<Map<String, Object>> selectExecutableAttorneyList(@Param(value="trusteeId") String var1);
}
