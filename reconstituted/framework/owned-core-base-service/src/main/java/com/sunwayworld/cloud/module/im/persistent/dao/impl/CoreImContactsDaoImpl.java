/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.persistent.dao.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsBean;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImContactsDao;
import com.sunwayworld.cloud.module.im.persistent.mapper.CoreImContactsMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImContactsDaoImpl
extends MybatisDaoSupport<CoreImContactsBean, Long>
implements CoreImContactsDao {
    @Autowired
    private CoreImContactsMapper coreImContactsMapper;

    public CoreImContactsMapper getMapper() {
        return this.coreImContactsMapper;
    }
}
