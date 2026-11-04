/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailDaoImpl
extends MybatisDaoSupport<CoreEmailBean, Long>
implements CoreEmailDao {
    @Autowired
    private CoreEmailMapper emailMapper;

    public CoreEmailMapper getMapper() {
        return this.emailMapper;
    }
}
