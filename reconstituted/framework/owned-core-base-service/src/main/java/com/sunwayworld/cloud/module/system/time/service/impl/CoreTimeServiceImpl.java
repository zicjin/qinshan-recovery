/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.system.time.service.impl;

import com.sunwayworld.cloud.module.system.time.service.CoreTimeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.TimeException;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTimeServiceImpl
implements CoreTimeService {
    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public LocalDateTime getServerTime() {
        return LocalDateTime.now();
    }

    @Override
    @Scheduled(fixedDelay=600000L)
    public void checkTimeSimultaneous() {
        long intervals = NumberUtils.parseInt(ApplicationContextHelper.getConstantValue("SYS_AMTD_IN_MS"));
        if (intervals <= 0L) {
            return;
        }
        LocalDateTime databaseTime = this.globalMapper.selectDatabaseTime();
        LocalDateTime now = LocalDateTime.now();
        long dValue = ConvertUtils.convert(databaseTime, Long.class) - ConvertUtils.convert(now, Long.class);
        if (Math.abs(dValue) >= intervals) {
            throw new TimeException("Time deviation is unexpected. Application system time is [" + now + "] but the database system time is [" + databaseTime + "].");
        }
    }
}
