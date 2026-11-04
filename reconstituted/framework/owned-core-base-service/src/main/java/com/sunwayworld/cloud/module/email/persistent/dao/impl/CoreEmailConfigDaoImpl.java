/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailConfigDaoImpl
extends MybatisDaoSupport<CoreEmailConfigBean, String>
implements CoreEmailConfigDao {
    @Autowired
    private CoreEmailConfigMapper configMapper;

    public CoreEmailConfigMapper getMapper() {
        return this.configMapper;
    }
}
