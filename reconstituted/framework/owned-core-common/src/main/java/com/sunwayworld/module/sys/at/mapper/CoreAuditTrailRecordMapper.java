/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAuditTrailRecordMapper
extends GenericMapper<Long> {
    public List<CoreAuditTraceOverviewDTO> selectTraceOverview(@Param(value="targetId") String var1, @Param(value="tableName") String var2, @Param(value="nameField") String var3);
}
