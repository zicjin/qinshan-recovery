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
public interface CoreBpmnCommentMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectCommentList(@Param(value="targetId") String var1);

    public List<String> selectUserTaskAuditedByList(@Param(value="targetId") String var1, @Param(value="statusCode") String var2);
}
