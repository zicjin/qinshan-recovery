/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreVerifyCalibratePlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreVerifyCalibratePlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreVerifyCalibratePlanDaoImpl
extends MybatisDaoSupport<CoreVerifyCalibratePlanBean, Long>
implements CoreVerifyCalibratePlanDao {
    @Autowired
    private CoreVerifyCalibratePlanMapper coreVerifyCalibratePlanMapper;

    public CoreVerifyCalibratePlanMapper getMapper() {
        return this.coreVerifyCalibratePlanMapper;
    }
}
