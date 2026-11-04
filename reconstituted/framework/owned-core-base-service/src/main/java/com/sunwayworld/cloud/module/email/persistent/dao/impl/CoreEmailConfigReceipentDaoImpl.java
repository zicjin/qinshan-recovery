/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigReceipentBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigReceipentDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailConfigReceipentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailConfigReceipentDaoImpl
extends MybatisDaoSupport<CoreEmailConfigReceipentBean, Long>
implements CoreEmailConfigReceipentDao {
    @Autowired
    private CoreEmailConfigReceipentMapper configReceipentMapper;

    public CoreEmailConfigReceipentMapper getMapper() {
        return this.configReceipentMapper;
    }
}
