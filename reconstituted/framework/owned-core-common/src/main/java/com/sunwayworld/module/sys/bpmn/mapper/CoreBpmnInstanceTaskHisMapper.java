/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreBpmnInstanceTaskHisMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectLatestTaskHisList(@Param(value="filterList") List<CoreBpmnInstanceTaskHisBean> var1);
}
