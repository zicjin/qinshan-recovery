/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageBean;
import com.sunwayworld.cloud.module.item.message.persistent.dao.CoreMessageDao;
import com.sunwayworld.cloud.module.item.message.persistent.mapper.CoreMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMessageDaoImpl
extends MybatisDaoSupport<CoreMessageBean, Long>
implements CoreMessageDao {
    @Autowired
    private CoreMessageMapper coreMessageMapper;

    public CoreMessageMapper getMapper() {
        return this.coreMessageMapper;
    }
}
