/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAuditLogColumnMapper
extends GenericMapper<Long> {
    public List<CoreAuditLogColumnNameDTO> selectDeletedNames(@Param(value="list") List<String> var1, @Param(value="tableName") String var2, @Param(value="nameField") String var3);

    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(@Param(value="tableName") String var1, @Param(value="targetId") String var2);
}
