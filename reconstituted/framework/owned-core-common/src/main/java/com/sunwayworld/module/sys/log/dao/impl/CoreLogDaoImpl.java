/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import com.sunwayworld.module.sys.log.dao.CoreLogDao;
import com.sunwayworld.module.sys.log.mapper.CoreLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLogDaoImpl
extends MybatisDaoSupport<CoreLogBean, Long>
implements CoreLogDao {
    @Autowired
    private CoreLogMapper mapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return this.mapper;
    }
}
