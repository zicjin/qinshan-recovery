/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.persistent.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import java.util.List;
import java.util.Map;

@GikamBean
public interface CoreEquiptUsageRecordMapper
extends GenericMapper<Long> {
    public List<Map<String, Object>> selectUsageTimeByYear(MapperParameter var1);

    public List<Map<String, Object>> selectUsageTimeByMonth();

    public List<Map<String, Object>> selectRecordByDate(MapperParameter var1);

    public List<Map<String, Object>> selectAmountGroupByMonth();

    public List<Map<String, Object>> selectAmountGroupByEquiptCategory(MapperParameter var1);

    public List<Map<String, Object>> selectContributionByDate(MapperParameter var1);
}
