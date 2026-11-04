/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreAuditTrailRecordLineMapper
extends GenericMapper<Long> {
    public List<String> selectTableNameByRecordId(String var1);

    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(@Param(value="list") List<String> var1, @Param(value="nameField") String var2);
}
