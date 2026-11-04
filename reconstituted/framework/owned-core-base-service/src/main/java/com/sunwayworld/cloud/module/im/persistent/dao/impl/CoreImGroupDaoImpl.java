/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.persistent.dao.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImGroupBean;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImGroupDao;
import com.sunwayworld.cloud.module.im.persistent.mapper.CoreImGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImGroupDaoImpl
extends MybatisDaoSupport<CoreImGroupBean, Long>
implements CoreImGroupDao {
    @Autowired
    private CoreImGroupMapper coreImGroupMapper;

    public CoreImGroupMapper getMapper() {
        return this.coreImGroupMapper;
    }
}
