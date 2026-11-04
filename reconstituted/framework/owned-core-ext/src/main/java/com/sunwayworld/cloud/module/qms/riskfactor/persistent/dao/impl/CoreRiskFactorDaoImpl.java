/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorBean;
import com.sunwayworld.cloud.module.qms.riskfactor.persistent.dao.CoreRiskFactorDao;
import com.sunwayworld.cloud.module.qms.riskfactor.persistent.mapper.CoreRiskFactorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreRiskFactorDaoImpl
extends MybatisDaoSupport<CoreRiskFactorBean, Long>
implements CoreRiskFactorDao {
    @Autowired
    private CoreRiskFactorMapper coreRiskFactorMapper;

    public CoreRiskFactorMapper getMapper() {
        return this.coreRiskFactorMapper;
    }
}
