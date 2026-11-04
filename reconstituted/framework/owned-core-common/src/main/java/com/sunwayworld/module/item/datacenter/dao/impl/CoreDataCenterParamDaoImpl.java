/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterParamDao;
import com.sunwayworld.module.item.datacenter.mapper.CoreDataCenterParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDataCenterParamDaoImpl
extends MybatisDaoSupport<CoreDataCenterParamBean, Long>
implements CoreDataCenterParamDao {
    @Autowired
    private CoreDataCenterParamMapper coreDataCenterParamMapper;

    public CoreDataCenterParamMapper getMapper() {
        return this.coreDataCenterParamMapper;
    }
}
