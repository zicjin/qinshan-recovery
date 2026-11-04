/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskDao;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.mapper.CoreScheduleTaskMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreScheduleTaskDaoImpl
extends MybatisDaoSupport<CoreScheduleTaskBean, String>
implements CoreScheduleTaskDao {
    @Autowired
    private CoreScheduleTaskMapper mapper;

    public CoreScheduleTaskMapper getMapper() {
        return this.mapper;
    }

    @Override
    @Cacheable(value={"T_CORE_SCHEDULE_TASK"}, key="#id")
    public CoreScheduleTaskBean selectByIdIfPresent(String id) {
        return (CoreScheduleTaskBean)super.selectByIdIfPresent(id);
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_SCHEDULE_TASK"}, keyGenerator="taskIdKeyGenerator")})
    public void cacheEvict(CoreScheduleTaskBean oldItem, CoreScheduleTaskBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_SCHEDULE_TASK", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }

    @Bean(value={"taskIdKeyGenerator"})
    public KeyGenerator taskIdKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreScheduleTaskBean)p[0], (CoreScheduleTaskBean)p[1]);
    }

    private String getRedisKey(CoreScheduleTaskBean oldItem, CoreScheduleTaskBean newItem) {
        CoreScheduleTaskBean item = Optional.ofNullable(oldItem).orElse(newItem);
        return String.valueOf(item.getId());
    }
}
