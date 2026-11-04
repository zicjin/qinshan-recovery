/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.persistent.dao.CoreTrainDao;
import com.sunwayworld.cloud.module.train.train.persistent.mapper.CoreTrainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTrainDaoImpl
extends MybatisDaoSupport<CoreTrainBean, Long>
implements CoreTrainDao {
    @Autowired
    private CoreTrainMapper coreTrainMapper;

    public CoreTrainMapper getMapper() {
        return this.coreTrainMapper;
    }
}
