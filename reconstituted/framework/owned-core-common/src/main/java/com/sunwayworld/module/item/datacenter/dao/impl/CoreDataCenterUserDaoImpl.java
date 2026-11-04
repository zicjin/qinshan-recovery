/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterUserDao;
import com.sunwayworld.module.item.datacenter.mapper.CoreDataCenterUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDataCenterUserDaoImpl
extends MybatisDaoSupport<CoreDataCenterUserBean, Long>
implements CoreDataCenterUserDao {
    @Autowired
    private CoreDataCenterUserMapper dataCenterUserMapper;

    public CoreDataCenterUserMapper getMapper() {
        return this.dataCenterUserMapper;
    }
}
