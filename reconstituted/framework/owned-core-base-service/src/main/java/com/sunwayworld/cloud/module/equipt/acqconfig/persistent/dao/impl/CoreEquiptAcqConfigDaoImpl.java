/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.acqconfig.bean.CoreEquiptAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.acqconfig.persistent.dao.CoreEquiptAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.acqconfig.persistent.mapper.CoreEquiptAcqConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptAcqConfigDaoImpl
extends MybatisDaoSupport<CoreEquiptAcqConfigBean, String>
implements CoreEquiptAcqConfigDao {
    @Autowired
    private CoreEquiptAcqConfigMapper coreEquiptAcqConfigMapper;

    public CoreEquiptAcqConfigMapper getMapper() {
        return this.coreEquiptAcqConfigMapper;
    }
}
