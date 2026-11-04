/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.CoreLaunchNewProjectItemDao;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.mapper.CoreLaunchNewProjectItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLaunchNewProjectItemDaoImpl
extends MybatisDaoSupport<CoreLaunchNewProjectItemBean, Long>
implements CoreLaunchNewProjectItemDao {
    @Autowired
    private CoreLaunchNewProjectItemMapper coreLaunchNewProjectItemMapper;

    public CoreLaunchNewProjectItemMapper getMapper() {
        return this.coreLaunchNewProjectItemMapper;
    }
}
