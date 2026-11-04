/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailFileBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailFileDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailFileMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEmailFileDaoImpl
extends MybatisDaoSupport<CoreEmailFileBean, Long>
implements CoreEmailFileDao {
    @Autowired
    private CoreEmailFileMapper coreEmailFileMapper;

    public CoreEmailFileMapper getMapper() {
        return this.coreEmailFileMapper;
    }
}
