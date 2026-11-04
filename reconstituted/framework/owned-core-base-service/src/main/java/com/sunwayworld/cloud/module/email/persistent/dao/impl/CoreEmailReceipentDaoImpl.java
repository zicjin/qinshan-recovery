/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailReceipentDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailReceipentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailReceipentDaoImpl
extends MybatisDaoSupport<CoreEmailReceipentBean, Long>
implements CoreEmailReceipentDao {
    @Autowired
    private CoreEmailReceipentMapper emailReceipentMapper;

    public CoreEmailReceipentMapper getMapper() {
        return this.emailReceipentMapper;
    }
}
