/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveySumDao;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.mapper.CoreCustomSurveySumMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomSurveySumDaoImpl
extends MybatisDaoSupport<CoreCustomSurveySumBean, Long>
implements CoreCustomSurveySumDao {
    @Autowired
    private CoreCustomSurveySumMapper coreCustomSurveySumMapper;

    public CoreCustomSurveySumMapper getMapper() {
        return this.coreCustomSurveySumMapper;
    }
}
