/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionBean;
import com.sunwayworld.cloud.module.qms.prevention.persistent.dao.CorePreventionDao;
import com.sunwayworld.cloud.module.qms.prevention.persistent.mapper.CorePreventionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CorePreventionDaoImpl
extends MybatisDaoSupport<CorePreventionBean, Long>
implements CorePreventionDao {
    @Autowired
    private CorePreventionMapper corePreventionMapper;

    public CorePreventionMapper getMapper() {
        return this.corePreventionMapper;
    }
}
