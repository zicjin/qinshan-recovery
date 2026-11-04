/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigBean;
import com.sunwayworld.module.sys.dpconfig.dao.CoreDpConfigDao;
import com.sunwayworld.module.sys.dpconfig.mapper.CoreDpConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDpConfigDaoImpl
extends MybatisDaoSupport<CoreDpConfigBean, Long>
implements CoreDpConfigDao {
    @Autowired
    private CoreDpConfigMapper dpConfigMapper;

    public CoreDpConfigMapper getMapper() {
        return this.dpConfigMapper;
    }
}
