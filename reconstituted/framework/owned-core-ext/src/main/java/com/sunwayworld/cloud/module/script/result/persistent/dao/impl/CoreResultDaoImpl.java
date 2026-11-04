/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.persistent.dao.impl;

import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.cloud.module.script.result.persistent.dao.CoreResultDao;
import com.sunwayworld.cloud.module.script.result.persistent.mapper.CoreResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreResultDaoImpl
extends MybatisDaoSupport<CoreResultBean, Long>
implements CoreResultDao {
    @Autowired
    private CoreResultMapper coreResultMapper;

    public CoreResultMapper getMapper() {
        return this.coreResultMapper;
    }
}
