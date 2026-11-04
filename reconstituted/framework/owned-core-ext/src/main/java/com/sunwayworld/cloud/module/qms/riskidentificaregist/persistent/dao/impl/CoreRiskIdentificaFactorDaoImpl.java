/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.persistent.dao.CoreRiskIdentificaFactorDao;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.persistent.mapper.CoreRiskIdentificaFactorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreRiskIdentificaFactorDaoImpl
extends MybatisDaoSupport<CoreRiskIdentificaFactorBean, Long>
implements CoreRiskIdentificaFactorDao {
    @Autowired
    private CoreRiskIdentificaFactorMapper coreRiskIdentificaFactorMapper;

    public CoreRiskIdentificaFactorMapper getMapper() {
        return this.coreRiskIdentificaFactorMapper;
    }
}
