/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddMessageBean;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.ZwddMessageDao;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.mapper.ZwddMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class ZwddMessageDaoImpl
extends MybatisDaoSupport<ZwddMessageBean, Long>
implements ZwddMessageDao {
    @Autowired
    private ZwddMessageMapper zwddMessageMapper;

    public ZwddMessageMapper getMapper() {
        return this.zwddMessageMapper;
    }
}
