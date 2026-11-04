/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterDao;
import com.sunwayworld.module.item.datacenter.mapper.CoreDataCenterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDataCenterDaoImpl
extends MybatisDaoSupport<CoreDataCenterBean, Long>
implements CoreDataCenterDao {
    @Autowired
    private CoreDataCenterMapper dataCenterMapper;

    public CoreDataCenterMapper getMapper() {
        return this.dataCenterMapper;
    }
}
