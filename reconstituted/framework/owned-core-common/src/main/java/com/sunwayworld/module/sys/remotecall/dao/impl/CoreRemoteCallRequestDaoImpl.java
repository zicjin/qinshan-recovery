/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallRequestDao;
import com.sunwayworld.module.sys.remotecall.mapper.CoreRemoteCallRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRemoteCallRequestDaoImpl
extends MybatisDaoSupport<CoreRemoteCallRequestBean, Long>
implements CoreRemoteCallRequestDao {
    @Autowired
    private CoreRemoteCallRequestMapper mapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return this.mapper;
    }
}
