/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageReceipentBean;
import com.sunwayworld.cloud.module.item.message.persistent.dao.CoreMessageReceipentDao;
import com.sunwayworld.cloud.module.item.message.persistent.mapper.CoreMessageReceipentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMessageReceipentDaoImpl
extends MybatisDaoSupport<CoreMessageReceipentBean, Long>
implements CoreMessageReceipentDao {
    @Autowired
    private CoreMessageReceipentMapper coreMessageReceipentMapper;

    public CoreMessageReceipentMapper getMapper() {
        return this.coreMessageReceipentMapper;
    }
}
