/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiVerifyReasonDao;
import com.sunwayworld.cloud.module.nfssi.order.persistent.mapper.CoreNfssiVerifyReasonMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiVerifyReasonDaoImpl
extends MybatisDaoSupport<CoreNfssiVerifyReasonBean, Long>
implements CoreNfssiVerifyReasonDao {
    @Autowired
    private CoreNfssiVerifyReasonMapper coreNfssiVerifyReasonMapper;

    public CoreNfssiVerifyReasonMapper getMapper() {
        return this.coreNfssiVerifyReasonMapper;
    }
}
