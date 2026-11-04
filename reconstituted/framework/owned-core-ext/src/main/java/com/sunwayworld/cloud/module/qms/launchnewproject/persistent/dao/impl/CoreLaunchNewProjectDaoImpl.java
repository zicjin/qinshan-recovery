/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.CoreLaunchNewProjectDao;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.mapper.CoreLaunchNewProjectMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLaunchNewProjectDaoImpl
extends MybatisDaoSupport<CoreLaunchNewProjectBean, Long>
implements CoreLaunchNewProjectDao {
    @Autowired
    private CoreLaunchNewProjectMapper coreLaunchNewProjectMapper;

    public CoreLaunchNewProjectMapper getMapper() {
        return this.coreLaunchNewProjectMapper;
    }
}
