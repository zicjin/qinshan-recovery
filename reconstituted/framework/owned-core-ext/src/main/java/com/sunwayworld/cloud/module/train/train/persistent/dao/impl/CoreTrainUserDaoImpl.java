/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.cloud.module.train.train.persistent.dao.CoreTrainUserDao;
import com.sunwayworld.cloud.module.train.train.persistent.mapper.CoreTrainUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTrainUserDaoImpl
extends MybatisDaoSupport<CoreTrainUserBean, Long>
implements CoreTrainUserDao {
    @Autowired
    private CoreTrainUserMapper coreTrainUserMapper;

    public CoreTrainUserMapper getMapper() {
        return this.coreTrainUserMapper;
    }
}
