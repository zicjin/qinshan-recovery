/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanUserBean;
import com.sunwayworld.cloud.module.train.trainplan.persistent.dao.CoreTrainPlanUserDao;
import com.sunwayworld.cloud.module.train.trainplan.persistent.mapper.CoreTrainPlanUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTrainPlanUserDaoImpl
extends MybatisDaoSupport<CoreTrainPlanUserBean, Long>
implements CoreTrainPlanUserDao {
    @Autowired
    private CoreTrainPlanUserMapper coreTrainPlanUserMapper;

    public CoreTrainPlanUserMapper getMapper() {
        return this.coreTrainPlanUserMapper;
    }
}
