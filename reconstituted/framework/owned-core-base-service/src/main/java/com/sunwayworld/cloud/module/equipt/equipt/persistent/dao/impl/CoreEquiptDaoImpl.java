/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.cloud.module.equipt.equipt.persistent.dao.CoreEquiptDao;
import com.sunwayworld.cloud.module.equipt.equipt.persistent.mapper.CoreEquiptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptDaoImpl
extends MybatisDaoSupport<CoreEquiptBean, Long>
implements CoreEquiptDao {
    @Autowired
    private CoreEquiptMapper equiptMapper;

    public CoreEquiptMapper getMapper() {
        return this.equiptMapper;
    }
}
