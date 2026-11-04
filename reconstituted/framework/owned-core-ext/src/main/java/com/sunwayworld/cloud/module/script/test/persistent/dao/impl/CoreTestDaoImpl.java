/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.persistent.dao.impl;

import com.sunwayworld.cloud.module.script.test.bean.CoreTestBean;
import com.sunwayworld.cloud.module.script.test.persistent.dao.CoreTestDao;
import com.sunwayworld.cloud.module.script.test.persistent.mapper.CoreTestMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTestDaoImpl
extends MybatisDaoSupport<CoreTestBean, Long>
implements CoreTestDao {
    @Autowired
    private CoreTestMapper coreTestMapper;

    public CoreTestMapper getMapper() {
        return this.coreTestMapper;
    }
}
