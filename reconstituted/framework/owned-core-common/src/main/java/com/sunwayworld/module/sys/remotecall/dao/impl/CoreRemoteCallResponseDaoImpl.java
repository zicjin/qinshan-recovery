/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallResponseDao;
import com.sunwayworld.module.sys.remotecall.mapper.CoreRemoteCallResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRemoteCallResponseDaoImpl
extends MybatisDaoSupport<CoreRemoteCallResponseBean, Long>
implements CoreRemoteCallResponseDao {
    @Autowired
    private CoreRemoteCallResponseMapper mapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return this.mapper;
    }
}
