/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface LcdpBizProcessNodeActionMapper
extends GenericMapper<Long> {
    public LcdpBizProcessNodeActionBean selectBpActionByBpId(@Param(value="bpId") Long var1);
}
