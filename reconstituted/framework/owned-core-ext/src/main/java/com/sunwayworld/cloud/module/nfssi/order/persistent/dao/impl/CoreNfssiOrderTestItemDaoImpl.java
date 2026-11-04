/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiOrderTestItemDao;
import com.sunwayworld.cloud.module.nfssi.order.persistent.mapper.CoreNfssiOrderTestItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiOrderTestItemDaoImpl
extends MybatisDaoSupport<CoreNfssiOrderTestItemBean, Long>
implements CoreNfssiOrderTestItemDao {
    @Autowired
    private CoreNfssiOrderTestItemMapper coreNfssiOrderTestItemMapper;

    public CoreNfssiOrderTestItemMapper getMapper() {
        return this.coreNfssiOrderTestItemMapper;
    }
}
