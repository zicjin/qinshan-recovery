/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumRetBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveySumRetDao;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.mapper.CoreCustomSurveySumRetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomSurveySumRetDaoImpl
extends MybatisDaoSupport<CoreCustomSurveySumRetBean, Long>
implements CoreCustomSurveySumRetDao {
    @Autowired
    private CoreCustomSurveySumRetMapper coreCustomSurveySumRetMapper;

    public CoreCustomSurveySumRetMapper getMapper() {
        return this.coreCustomSurveySumRetMapper;
    }
}
