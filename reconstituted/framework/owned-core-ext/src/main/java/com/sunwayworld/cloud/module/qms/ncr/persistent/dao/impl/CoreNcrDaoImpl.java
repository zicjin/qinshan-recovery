/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.ncr.bean.CoreNcrBean;
import com.sunwayworld.cloud.module.qms.ncr.persistent.dao.CoreNcrDao;
import com.sunwayworld.cloud.module.qms.ncr.persistent.mapper.CoreNcrMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNcrDaoImpl
extends MybatisDaoSupport<CoreNcrBean, Long>
implements CoreNcrDao {
    @Autowired
    private CoreNcrMapper coreNcrMapper;

    public CoreNcrMapper getMapper() {
        return this.coreNcrMapper;
    }
}
