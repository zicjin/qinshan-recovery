/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.cloud.module.qms.usertrain.persistent.dao.CoreUserTrainDao;
import com.sunwayworld.cloud.module.qms.usertrain.persistent.mapper.CoreUserTrainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreUserTrainDaoImpl
extends MybatisDaoSupport<CoreUserTrainBean, Long>
implements CoreUserTrainDao {
    @Autowired
    private CoreUserTrainMapper coreUserTrainMapper;

    public CoreUserTrainMapper getMapper() {
        return this.coreUserTrainMapper;
    }
}
