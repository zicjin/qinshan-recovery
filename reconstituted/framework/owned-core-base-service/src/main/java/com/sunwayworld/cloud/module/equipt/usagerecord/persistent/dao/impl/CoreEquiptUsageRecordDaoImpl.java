/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.cloud.module.equipt.usagerecord.persistent.dao.CoreEquiptUsageRecordDao;
import com.sunwayworld.cloud.module.equipt.usagerecord.persistent.mapper.CoreEquiptUsageRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptUsageRecordDaoImpl
extends MybatisDaoSupport<CoreEquiptUsageRecordBean, Long>
implements CoreEquiptUsageRecordDao {
    @Autowired
    private CoreEquiptUsageRecordMapper coreEquiptUsageRecordMapper;

    public CoreEquiptUsageRecordMapper getMapper() {
        return this.coreEquiptUsageRecordMapper;
    }

    @Override
    public List<Map<String, Object>> selectUsageTimeByYear(MapperParameter parameter) {
        return this.coreEquiptUsageRecordMapper.selectUsageTimeByYear(parameter);
    }

    @Override
    public List<Map<String, Object>> selectUsageTimeByMonth() {
        return this.coreEquiptUsageRecordMapper.selectUsageTimeByMonth();
    }

    @Override
    public List<Map<String, Object>> selectRecordByDate(MapperParameter parameter) {
        return this.coreEquiptUsageRecordMapper.selectRecordByDate(parameter);
    }

    @Override
    public List<Map<String, Object>> selectAmountGroupByMonth() {
        return this.coreEquiptUsageRecordMapper.selectAmountGroupByMonth();
    }

    @Override
    public List<Map<String, Object>> selectAmountGroupByEquiptCategory(MapperParameter parameter) {
        return this.coreEquiptUsageRecordMapper.selectAmountGroupByEquiptCategory(parameter);
    }

    @Override
    public List<Map<String, Object>> selectContributionByDate(MapperParameter parameter) {
        return this.coreEquiptUsageRecordMapper.selectContributionByDate(parameter);
    }
}
