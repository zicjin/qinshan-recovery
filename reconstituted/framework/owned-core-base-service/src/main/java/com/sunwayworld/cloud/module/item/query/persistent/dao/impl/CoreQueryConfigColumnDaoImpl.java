/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigColumnBean;
import com.sunwayworld.cloud.module.item.query.persistent.dao.CoreQueryConfigColumnDao;
import com.sunwayworld.cloud.module.item.query.persistent.mapper.CoreQueryConfigColumnMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreQueryConfigColumnDaoImpl
extends MybatisDaoSupport<CoreQueryConfigColumnBean, Long>
implements CoreQueryConfigColumnDao {
    @Autowired
    private CoreQueryConfigColumnMapper mapper;

    public CoreQueryConfigColumnMapper getMapper() {
        return this.mapper;
    }
}
