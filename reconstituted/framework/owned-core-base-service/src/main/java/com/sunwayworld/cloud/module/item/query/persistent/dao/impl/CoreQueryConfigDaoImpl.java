/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.cloud.module.item.query.persistent.dao.CoreQueryConfigDao;
import com.sunwayworld.cloud.module.item.query.persistent.mapper.CoreQueryConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreQueryConfigDaoImpl
extends MybatisDaoSupport<CoreQueryConfigBean, String>
implements CoreQueryConfigDao {
    @Autowired
    private CoreQueryConfigMapper mapper;

    public CoreQueryConfigMapper getMapper() {
        return this.mapper;
    }
}
