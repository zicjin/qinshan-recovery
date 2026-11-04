/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.persistent.dao.impl;

import com.sunwayworld.cloud.module.topology.bean.CoreTopologyBean;
import com.sunwayworld.cloud.module.topology.persistent.dao.CoreTopologyDao;
import com.sunwayworld.cloud.module.topology.persistent.mapper.CoreTopologyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTopologyDaoImpl
extends MybatisDaoSupport<CoreTopologyBean, Long>
implements CoreTopologyDao {
    @Autowired
    private CoreTopologyMapper mapper;

    public CoreTopologyMapper getMapper() {
        return this.mapper;
    }
}
