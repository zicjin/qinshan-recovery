/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreBpmnProcMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectRuntimeBpmnProcList(@Param(value="tableName") String var1, @Param(value="usedOrgId") String var2);

    public List<Map<String, Object>> selectBpmnProcListByServiceId(@Param(value="serviceId") String var1);
}
