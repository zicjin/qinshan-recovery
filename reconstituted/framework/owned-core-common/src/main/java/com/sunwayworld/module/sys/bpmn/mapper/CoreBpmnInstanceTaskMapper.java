/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;

@GikamBean
public interface CoreBpmnInstanceTaskMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectAuditableInstanceTaskList(@Nullable @Param(value="userId") String var1, @Param(value="instIdList") List<Long> var2);
}
