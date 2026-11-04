/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioBlockDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioBlockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioBlockDaoImpl
extends MybatisDaoSupport<CoreStudioBlockBean, Long>
implements CoreStudioBlockDao {
    @Autowired
    private CoreStudioBlockMapper studioBlockMapper;

    public CoreStudioBlockMapper getMapper() {
        return this.studioBlockMapper;
    }
}
