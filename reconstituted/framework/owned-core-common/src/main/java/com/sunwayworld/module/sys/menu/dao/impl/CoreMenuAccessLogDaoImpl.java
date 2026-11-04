/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuAccessLogBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuAccessLogDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuAccessLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuAccessLogDaoImpl
extends MybatisDaoSupport<CoreMenuAccessLogBean, Long>
implements CoreMenuAccessLogDao {
    @Autowired
    private CoreMenuAccessLogMapper menuAccessLogMapper;

    public CoreMenuAccessLogMapper getMapper() {
        return this.menuAccessLogMapper;
    }
}
