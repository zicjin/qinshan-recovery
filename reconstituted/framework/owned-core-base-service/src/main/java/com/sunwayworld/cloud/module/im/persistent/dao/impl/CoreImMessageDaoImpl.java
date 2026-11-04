/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.persistent.dao.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImMessageDao;
import com.sunwayworld.cloud.module.im.persistent.mapper.CoreImMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImMessageDaoImpl
extends MybatisDaoSupport<CoreImMessageBean, Long>
implements CoreImMessageDao {
    @Autowired
    private CoreImMessageMapper coreImMessageMapper;

    public CoreImMessageMapper getMapper() {
        return this.coreImMessageMapper;
    }
}
