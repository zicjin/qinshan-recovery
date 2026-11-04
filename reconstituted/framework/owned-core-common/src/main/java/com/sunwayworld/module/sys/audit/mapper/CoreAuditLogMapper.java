/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAuditLogMapper
extends GenericMapper<Long> {
    public List<CoreAuditLogTraceOverviewDTO> selectTraceOverview(@Param(value="targetId") String var1, @Param(value="tableName") String var2, @Param(value="nameField") String var3);
}
