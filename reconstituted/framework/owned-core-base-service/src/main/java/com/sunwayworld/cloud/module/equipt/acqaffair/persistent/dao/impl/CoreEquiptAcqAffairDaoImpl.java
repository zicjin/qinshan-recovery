/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqaffair.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.acqaffair.bean.CoreEquiptAcqAffairBean;
import com.sunwayworld.cloud.module.equipt.acqaffair.persistent.dao.CoreEquiptAcqAffairDao;
import com.sunwayworld.cloud.module.equipt.acqaffair.persistent.mapper.CoreEquiptAcqAffairMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptAcqAffairDaoImpl
extends MybatisDaoSupport<CoreEquiptAcqAffairBean, String>
implements CoreEquiptAcqAffairDao {
    @Autowired
    private CoreEquiptAcqAffairMapper coreEquiptAcqAffairMapper;

    public CoreEquiptAcqAffairMapper getMapper() {
        return this.coreEquiptAcqAffairMapper;
    }
}
