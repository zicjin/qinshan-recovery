/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiTestReasonDao;
import com.sunwayworld.cloud.module.nfssi.order.persistent.mapper.CoreNfssiTestReasonMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiTestReasonDaoImpl
extends MybatisDaoSupport<CoreNfssiTestReasonBean, Long>
implements CoreNfssiTestReasonDao {
    @Autowired
    private CoreNfssiTestReasonMapper coreNfssiTestReasonMapper;

    public CoreNfssiTestReasonMapper getMapper() {
        return this.coreNfssiTestReasonMapper;
    }
}
