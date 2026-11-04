/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.bean.CoreLoginLinkBean;
import com.sunwayworld.module.sys.config.dao.CoreLoginLinkDao;
import com.sunwayworld.module.sys.config.mapper.CoreLoginLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLoginLinkDaoImpl
extends MybatisDaoSupport<CoreLoginLinkBean, Long>
implements CoreLoginLinkDao {
    @Autowired
    private CoreLoginLinkMapper coreLoginLinkMapper;

    public CoreLoginLinkMapper getMapper() {
        return this.coreLoginLinkMapper;
    }
}
