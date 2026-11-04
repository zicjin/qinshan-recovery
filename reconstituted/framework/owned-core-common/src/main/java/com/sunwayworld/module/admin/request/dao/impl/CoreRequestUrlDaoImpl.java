/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.admin.request.dao.CoreRequestUrlDao;
import com.sunwayworld.module.admin.request.mapper.CoreRequestUrlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreRequestUrlDaoImpl
extends MybatisDaoSupport<CoreRequestUrlBean, Long>
implements CoreRequestUrlDao {
    @Autowired
    private CoreRequestUrlMapper coreRequestUrlMapper;

    public CoreRequestUrlMapper getMapper() {
        return this.coreRequestUrlMapper;
    }

    @Override
    public void cacheEvict(CoreRequestUrlBean oldItem, CoreRequestUrlBean newItem) {
        String requestUrl;
        String string = requestUrl = oldItem == null ? newItem.getUrl() : oldItem.getUrl();
        if (!StringUtils.isBlank(requestUrl)) {
            RedisHelper.evict("T_CORE_REQUEST_URL.BY_REQUEST_URL", requestUrl);
        }
    }
}
