/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.cloud.module.train.trainplan.persistent.dao.CoreTrainPlanDao;
import com.sunwayworld.cloud.module.train.trainplan.persistent.mapper.CoreTrainPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTrainPlanDaoImpl
extends MybatisDaoSupport<CoreTrainPlanBean, Long>
implements CoreTrainPlanDao {
    @Autowired
    private CoreTrainPlanMapper coreTrainPlanMapper;

    public CoreTrainPlanMapper getMapper() {
        return this.coreTrainPlanMapper;
    }
}
