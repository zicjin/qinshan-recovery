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
public interface CoreBpmnInstanceMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectInstanceAuditorList(@Param(value="idList") List<Long> var1);

    public List<Map<String, Object>> selectInstanceTaskAuditorList(@Param(value="idList") List<Long> var1);

    public List<Map<String, Object>> selectInstanceDetailedCandidatorList(@Param(value="idList") List<Long> var1);
}
