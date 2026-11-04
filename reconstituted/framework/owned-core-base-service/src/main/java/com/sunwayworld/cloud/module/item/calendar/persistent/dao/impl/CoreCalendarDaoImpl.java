/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarBean;
import com.sunwayworld.cloud.module.item.calendar.persistent.dao.CoreCalendarDao;
import com.sunwayworld.cloud.module.item.calendar.persistent.mapper.CoreCalendarMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreCalendarDaoImpl
extends MybatisDaoSupport<CoreCalendarBean, String>
implements CoreCalendarDao {
    @Autowired
    private CoreCalendarMapper calendarMapper;

    public CoreCalendarMapper getMapper() {
        return this.calendarMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_CALENDAR"}, key="#id", unless="#result == null")
    public CoreCalendarBean selectByIdIfPresent(String id) {
        return (CoreCalendarBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_CALENDAR"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreCalendarBean oldItem, CoreCalendarBean newItem) {
    }
}
