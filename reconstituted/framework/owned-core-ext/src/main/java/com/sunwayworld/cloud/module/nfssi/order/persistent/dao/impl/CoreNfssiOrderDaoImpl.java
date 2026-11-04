/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiOrderDao;
import com.sunwayworld.cloud.module.nfssi.order.persistent.mapper.CoreNfssiOrderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiOrderDaoImpl
extends MybatisDaoSupport<CoreNfssiOrderBean, String>
implements CoreNfssiOrderDao {
    @Autowired
    private CoreNfssiOrderMapper coreNfssiOrderMapper;

    public CoreNfssiOrderMapper getMapper() {
        return this.coreNfssiOrderMapper;
    }
}
