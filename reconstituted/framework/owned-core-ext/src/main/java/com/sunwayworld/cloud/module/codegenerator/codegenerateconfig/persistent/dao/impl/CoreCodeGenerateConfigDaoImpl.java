/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.mapper.CoreCodeGenerateConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCodeGenerateConfigDaoImpl
extends MybatisDaoSupport<CoreCodeGenerateConfigBean, String>
implements CoreCodeGenerateConfigDao {
    @Autowired
    private CoreCodeGenerateConfigMapper configMapper;

    public CoreCodeGenerateConfigMapper getMapper() {
        return this.configMapper;
    }
}
